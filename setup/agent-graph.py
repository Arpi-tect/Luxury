# LangGraph Orchestration for Luxury — Royal Concierge Multi-Agent DAG
# Equip with intent routing, RAG match, loyalty pricing, upsell, and database SQL tools.

from typing import TypedDict, Annotated, Sequence, Dict, Any
from langchain_core.messages import BaseMessage, HumanMessage, AIMessage
from langchain_openai import OpenAIEmbeddings, ChatOpenAI
from langgraph.graph import StateGraph, END
import json

# 1. State Definition
class AgentState(TypedDict):
    messages: Sequence[BaseMessage]
    current_intent: str
    guest_profile: Dict[str, Any]
    selected_rooms: list
    pricing_estimate: Dict[str, Any]
    loyalty_tier: str
    session_id: str

# 2. Node Functions

# A. Intent Router Agent Node: Identifies guest intent (search, book, upsell, support)
def intent_router_node(state: AgentState) -> Dict[str, Any]:
    messages = state["messages"]
    last_message = messages[-1].content
    
    # Simulate LLM Classification
    llm = ChatOpenAI(model="gpt-4o-mini", temperature=0.0)
    system_prompt = (
        "Classify the user intent into one of: 'search_suites', 'pricing_loyalty', "
        "'upsell_perks', 'booking_support', or 'admin_analytics'. Response must be JSON."
    )
    classification = llm.invoke([
        HumanMessage(content=system_prompt),
        HumanMessage(content=last_message)
    ])
    
    try:
        data = json.loads(classification.content)
        intent = data.get("intent", "search_suites")
    except Exception:
        intent = "search_suites"
        
    return {"current_intent": intent}

# B. Room Match Agent Node: Queries vector database embeddings for similar rooms
def room_match_node(state: AgentState) -> Dict[str, Any]:
    last_message = state["messages"][-1].content
    
    # Simulated vector similarity lookup using pgvector
    # query_embeddings = OpenAIEmbeddings().embed_query(last_message)
    # rooms = prisma.query_raw("SELECT * FROM Room ORDER BY embedding <=> $1 LIMIT 3")
    
    mock_results = [
        {"roomNumber": 301, "category": "Suite", "pricePerNight": 350.0, "amenities": ["Canopy Bed", "Balcony Jacuzzi"]},
        {"roomNumber": 201, "category": "Deluxe", "pricePerNight": 180.0, "amenities": ["City view balcony", "Lounge sofa"]}
    ]
    
    response_content = (
        f"Based on your requirements, I have matched the following suites:\n"
        f"1. Suite {mock_results[0]['roomNumber']} ({mock_results[0]['category']}) at ${mock_results[0]['pricePerNight']}/night.\n"
        f"2. Suite {mock_results[1]['roomNumber']} ({mock_results[1]['category']}) at ${mock_results[1]['pricePerNight']}/night."
    )
    
    return {
        "messages": [AIMessage(content=response_content)],
        "selected_rooms": mock_results
    }

# C. Pricing & Loyalty Agent Node: Applies discounts based on loyalty tier
def pricing_node(state: AgentState) -> Dict[str, Any]:
    tier = state.get("loyalty_tier", "Silver")
    rooms = state.get("selected_rooms", [])
    
    discount = 0.0
    if tier == "Platinum":
        discount = 0.15 # 15% VIP discount
    elif tier == "Gold":
        discount = 0.08 # 8% Gold discount
        
    rates = []
    for r in rooms:
        discounted_price = r["pricePerNight"] * (1.0 - discount)
        rates.append({
            "roomNumber": r["roomNumber"],
            "originalPrice": r["pricePerNight"],
            "discountedPrice": discounted_price,
            "tierDiscount": f"{int(discount*100)}%"
        })
        
    response_content = (
        f"As a valued {tier} guest, I have applied your exclusive tier privileges. "
        f"Your updated rates are: ${rates[0]['discountedPrice']}/night (originally ${rates[0]['originalPrice']})."
    )
    
    return {
        "messages": [AIMessage(content=response_content)],
        "pricing_estimate": {"discounted_rates": rates, "applied_discount": discount}
    }

# D. Upsell Agent Node: Promotes high-end additions (Spa pass, shuttle)
def upsell_node(state: AgentState) -> Dict[str, Any]:
    # Custom upsell proposals based on matched suites
    upsell_offer = (
        "May I recommend our signature VIP Wellness Spa pass ($50 flat) or our private "
        "Palace Airport Shuttle service ($30 flat) to finalize your presidential treatment?"
    )
    return {"messages": [AIMessage(content=upsell_offer)]}

# E. Support Agent Node: Handles modifications & policy FAQS
def support_node(state: AgentState) -> Dict[str, Any]:
    faq_response = (
        "Refund privileges apply fully if reservations are cancelled at least 24 hours prior "
        "to check-in. Modifications to booking dates can be processed directly via your Guest Directory."
    )
    return {"messages": [AIMessage(content=faq_response)]}

# F. Admin Insight Node: SQL Generation and execution node for staff queries
def admin_insight_node(state: AgentState) -> Dict[str, Any]:
    # Mocking text-to-SQL execution
    # query_sql = "SELECT category, COUNT(*) FROM Booking JOIN Room ON Booking.roomId = Room.id GROUP BY category"
    # results = prisma.$queryRaw(query_sql)
    
    analytics_report = (
        "Occupancy distribution analytics reflect Suite categories leading demand (5 bookings), "
        "followed by Deluxe chambers (2 bookings), representing a gross revenue of $1,890.00."
    )
    return {"messages": [AIMessage(content=analytics_report)]}

# 3. Routing Edge Logic
def route_by_intent(state: AgentState) -> str:
    intent = state["current_intent"]
    if intent == "search_suites":
        return "room_match"
    elif intent == "pricing_loyalty":
        return "pricing"
    elif intent == "upsell_perks":
        return "upsell"
    elif intent == "booking_support":
        return "support"
    elif intent == "admin_analytics":
        return "admin_insight"
    return END

# 4. Construct LangGraph Workflow DAG
workflow = StateGraph(AgentState)

# Add Nodes
workflow.add_node("intent_router", intent_router_node)
workflow.add_node("room_match", room_match_node)
workflow.add_node("pricing", pricing_node)
workflow.add_node("upsell", upsell_node)
workflow.add_node("support", support_node)
workflow.add_node("admin_insight", admin_insight_node)

# Set Entry Point
workflow.set_entry_point("intent_router")

# Add Conditional Edges based on classification intent
workflow.add_conditional_edges(
    "intent_router",
    route_by_intent,
    {
        "room_match": "room_match",
        "pricing": "pricing",
        "upsell": "upsell",
        "support": "support",
        "admin_insight": "admin_insight",
        END: END
    }
)

# Connect terminal nodes to END
workflow.add_edge("room_match", END)
workflow.add_edge("pricing", END)
workflow.add_edge("upsell", END)
workflow.add_edge("support", END)
workflow.add_edge("admin_insight", END)

# Compile LangGraph app
app = workflow.compile()

print("LangGraph Concierge Orchestration compiled successfully.")
