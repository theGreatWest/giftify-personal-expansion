## 문제 1: DNS가 전파되지 않음

**증상:**
```bash
nslookup api.giftify.yjkim.store
# NXDOMAIN 에러 발생
```

**해결:**
```bash
# EC2에서 임시 hosts 파일 수정
sudo nano /etc/hosts
# 추가: 43.202.50.86 api.giftify.yjkim.store
```

## 문제 2: 컨테이너가 재시작 반복

**증상:**
```bash
docker ps -a
# STATUS: Restarting
```

**해결:**
```bash
# 로그 확인
docker logs container-giftify

# 환경변수 확인
docker inspect container-giftify | grep -A 10 Env

# DB 연결 테스트
docker exec -it postgres-giftify psql -U giftify -d giftify_db
```