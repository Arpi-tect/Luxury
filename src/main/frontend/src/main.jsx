import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx';
import './index.css';

const root = ReactDOM.createRoot(document.getElementById('app-root'));
root.render(<App/>);
setTimeout(() => {
  const l = document.getElementById('loader');
  if (l) l.classList.add('gone');
}, 500);