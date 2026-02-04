import { Link } from 'react-router-dom';

const Navbar: React.FC = () => {
  return (
    <nav style={{
      backgroundColor: '#f7cddd',
      padding: '1rem 2rem',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
    }}>
      <Link to="/" style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#333' }}>Giftify</Link>
      <div>
        <Link to="/signup">
          <button style={{
            backgroundColor: 'white',
            border: '1px solid #333',
            padding: '0.5rem 1rem',
            borderRadius: '4px',
            fontWeight: 'bold',
            cursor: 'pointer'
          }}>
            회원가입
          </button>
        </Link>
      </div>
    </nav>
  );
};

export default Navbar;
