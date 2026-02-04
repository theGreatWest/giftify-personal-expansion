import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Signup from './pages/Signup';
import './index.css';

function App() {
  const isVercel = import.meta.env.VITE_VERCEL === 'true' || window.location.hostname.includes('vercel.app');
  const basename = isVercel ? '' : '/giftify';

  return (
    <Router basename={basename}>
      <div className="App">
        <Navbar />
        <main className="container">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/signup" element={<Signup />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
