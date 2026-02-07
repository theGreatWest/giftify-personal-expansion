import React, { useState } from 'react';

const Home: React.FC = () => {
  const [input, setInput] = useState('');
  const [response, setResponse] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const apiUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
    
    try {
      const res = await fetch(`${apiUrl}/api/test`, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain',
        },
        body: input,
      });
      const data = await res.text();
      setResponse(data);
    } catch (error) {
      console.error('API Error:', error);
      setResponse('API 호출 중 오류가 발생했습니다.');
    }
  };

  return (
    <div style={{ padding: '2rem', textAlign: 'center' }}>
      <h1>Giftify에 오신 것을 환영합니다!</h1>
      
      <div style={{ marginTop: '2rem', padding: '1rem', border: '1px solid #ccc', borderRadius: '8px' }}>
        <h3>API 연동 테스트</h3>
        <form onSubmit={handleSubmit}>
          <input 
            type="text" 
            value={input} 
            onChange={(e) => setInput(e.target.value)} 
            placeholder="보낼 문장을 입력하세요"
            style={{ padding: '0.5rem', width: '250px' }}
          />
          <button type="submit" style={{ padding: '0.5rem 1rem', marginLeft: '0.5rem' }}>전송</button>
        </form>
        
        {response && (
          <div style={{ marginTop: '1rem', color: '#007bff' }}>
            <strong>응답:</strong> {response}
          </div>
        )}
      </div>
    </div>
  );
};

export default Home;
