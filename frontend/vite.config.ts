import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig(() => {
  const isVercel = process.env.VERCEL === 'true';
  return {
    plugins: [react()],
    base: isVercel ? '/' : '/giftify/',
  };
})
