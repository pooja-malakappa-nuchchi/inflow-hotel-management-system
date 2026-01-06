import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

/**
 * Application entry point.
 * Renders the root App component into the DOM with React 18's createRoot API.
 * StrictMode is enabled for development checks and warnings.
 */
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)