# minio 테스트 환경 구축

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
