// Stripe Payment Gateway Integration Boilerplate for Luxury App
const express = require('express');
const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

const router = express.Router();

// Endpoint to create a Stripe checkout session for a hotel reservation
router.post('/api/checkout/create-session', async (req, res) => {
  const { bookingId, customerEmail } = req.body;

  try {
    // 1. Fetch booking invoice total from postgres database
    const booking = await prisma.booking.findUnique({
      where: { id: bookingId },
      include: { room: true }
    });

    if (!booking) {
      return res.status(404).json({ error: 'Booking record not found' });
    }

    // 2. Create the Checkout Session with Stripe API
    const session = await stripe.checkout.sessions.create({
      payment_method_types: ['card'],
      customer_email: customerEmail,
      line_items: [
        {
          price_data: {
            currency: 'usd',
            product_data: {
              name: `Suite Room ${booking.room.roomNumber} - ${booking.room.category}`,
              description: `Palace booking check-in from ${booking.checkIn.toDateString()} to ${booking.checkOut.toDateString()}`,
            },
            unit_amount: Math.round(booking.totalPrice * 100), // Stripe expects amounts in cents
          },
          quantity: 1,
        },
      ],
      mode: 'payment',
      success_url: `${process.env.FRONTEND_URL}/booking/success?session_id={CHECKOUT_SESSION_ID}`,
      cancel_url: `${process.env.FRONTEND_URL}/booking/cancel`,
      metadata: {
        bookingId: bookingId,
      },
    });

    res.json({ sessionId: session.id, url: session.url });
  } catch (error) {
    console.error('Stripe session creation error:', error);
    res.status(500).json({ error: 'Failed to initialize payment gateway checkout session' });
  }
});

// Stripe Webhook Endpoint to handle payment success notifications asynchronously
router.post('/api/checkout/webhook', express.raw({ type: 'application/json' }), async (req, res) => {
  const sig = req.headers['stripe-signature'];
  let event;

  try {
    // Verify that the request came from Stripe using the signing secret
    event = stripe.webhooks.constructEvent(req.body, sig, process.env.STRIPE_WEBHOOK_SECRET);
  } catch (err) {
    console.error(`Webhook signature verification failed:`, err.message);
    return res.status(400).send(`Webhook Error: ${err.message}`);
  }

  // Handle checkout.session.completed event
  if (event.type === 'checkout.session.completed') {
    const session = event.data.object;
    const bookingId = session.metadata.bookingId;
    const paymentIntentId = session.payment_intent;

    try {
      // Run database operations inside a transaction
      await prisma.$transaction([
        // 1. Update Booking status to PAID
        prisma.booking.update({
          where: { id: bookingId },
          data: { status: 'PAID', qrCode: `BK-CONFIRMED-${bookingId}` }
        }),
        // 2. Log Payment transaction Details
        prisma.payment.create({
          data: {
            bookingId: bookingId,
            provider: 'stripe',
            status: 'SUCCESS',
            amount: session.amount_total / 100,
            transactionId: paymentIntentId
          }
        })
      ]);

      console.log(`Payment confirmed and recorded for booking ${bookingId}`);
    } catch (dbError) {
      console.error(`Database update failed for booking ${bookingId}:`, dbError);
      return res.status(500).send('Database logging failed');
    }
  }

  res.json({ received: true });
});

module.exports = router;
