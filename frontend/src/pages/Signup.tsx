import { useState } from 'react';

const Signup: React.FC = () => {
  const [formData, setFormData] = useState({
    email: '',
    phone: '',
    name: '',
    nickname: '',
    address: '',
    birthDate: '',
    password: '',
    confirmPassword: '',
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const validatePassword = (pass: string) => {
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])(?=.{8,16})/;
    return regex.test(pass);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const newErrors: Record<string, string> = {};

    if (!formData.email) newErrors.email = '이메일은 필수입니다.';
    if (!formData.phone) newErrors.phone = '핸드폰번호는 필수입니다.';
    if (!formData.name) newErrors.name = '이름은 필수입니다.';
    if (!formData.birthDate) newErrors.birthDate = '생년월일은 필수입니다.';
    if (!formData.password) {
      newErrors.password = '비밀번호는 필수입니다.';
    } else if (!validatePassword(formData.password)) {
      newErrors.password = '비밀번호는 8-16자이며 특수기호, 영어 대소문자를 모두 포함해야 합니다.';
    }
    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = '비밀번호가 일치하지 않습니다.';
    }

    setErrors(newErrors);

    if (Object.keys(newErrors).length === 0) {
      alert('회원가입 성공!');
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const inputStyle = {
    display: 'block',
    width: '100%',
    padding: '8px',
    marginBottom: '5px',
    borderRadius: '4px',
    border: '1px solid #ccc',
  };

  const errorStyle = {
    color: 'red',
    fontSize: '12px',
    marginBottom: '10px',
  };

  return (
    <div style={{ maxWidth: '400px', margin: '2rem auto', padding: '1rem', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: 'white' }}>
      <h2 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>회원가입</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>이메일 (필수, 인증 필요)</label>
          <input type="email" name="email" value={formData.email} onChange={handleChange} style={inputStyle} />
          {errors.email && <div style={errorStyle}>{errors.email}</div>}
        </div>
        <div>
          <label>핸드폰번호 (필수, 인증 필요)</label>
          <input type="text" name="phone" value={formData.phone} onChange={handleChange} style={inputStyle} />
          {errors.phone && <div style={errorStyle}>{errors.phone}</div>}
        </div>
        <div>
          <label>이름 (필수)</label>
          <input type="text" name="name" value={formData.name} onChange={handleChange} style={inputStyle} />
          {errors.name && <div style={errorStyle}>{errors.name}</div>}
        </div>
        <div>
          <label>닉네임</label>
          <input type="text" name="nickname" value={formData.nickname} onChange={handleChange} style={inputStyle} />
        </div>
        <div>
          <label>주소</label>
          <input type="text" name="address" value={formData.address} onChange={handleChange} style={inputStyle} />
        </div>
        <div>
          <label>생년월일 (필수)</label>
          <input type="date" name="birthDate" value={formData.birthDate} onChange={handleChange} style={inputStyle} />
          {errors.birthDate && <div style={errorStyle}>{errors.birthDate}</div>}
        </div>
        <div>
          <label>비밀번호 (8-16자, 특수문자, 대소문자 포함)</label>
          <input type="password" name="password" value={formData.password} onChange={handleChange} style={inputStyle} />
          {errors.password && <div style={errorStyle}>{errors.password}</div>}
        </div>
        <div>
          <label>비밀번호 확인</label>
          <input type="password" name="confirmPassword" value={formData.confirmPassword} onChange={handleChange} style={inputStyle} />
          {errors.confirmPassword && <div style={errorStyle}>{errors.confirmPassword}</div>}
        </div>
        <button type="submit" style={{ width: '100%', padding: '10px', backgroundColor: '#f7cddd', border: 'none', borderRadius: '4px', fontWeight: 'bold', cursor: 'pointer', marginTop: '1rem' }}>
          가입하기
        </button>
      </form>
    </div>
  );
};

export default Signup;
