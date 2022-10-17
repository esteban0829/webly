# minio 테스트 환경 구축

## 설치
도커

```bash
docker run \
  -p 9000:9000 \
  -p 9001:9001 \
  -e "MINIO_ROOT_USER=user" \
  -e "MINIO_ROOT_PASSWORD=password" \
  quay.io/minio/minio server /data --console-address ":9001"
```

쿠버네티스
```bash
kubectl apply -f ./minio.yaml
```
- 서비스를 NodePort로 변경하거나 사용중인 쿠버네티스 환경에서 적절히 포트포워딩해서 사용

## 실행
- 아래 VM 옵션이 없으면 프로그램 실행 시점에 EC2의 메타데이터를 읽다가 실패함
- 안써도 WARNING 로그 남고 실행은되나 불필요한 동작이므로 추가해주는 것이 좋음
- `-Dcom.amazonaws.sdk.disableEc2Metadata=true`

## Minio 설정
1. 계정 생성 
   - readwrite 권한
   - 계정 이름이 accessKey, 비밀번호가 secretKey
2. 버킷 생성 (web-clipboard)
3. `버킷 > Manage > Acess Audit > Users` 에서 계정 추가
