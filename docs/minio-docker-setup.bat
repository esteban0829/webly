docker run ^
  -d ^
  -p 9000:9000 ^
  -p 9001:9001 ^
  -e "MINIO_ROOT_USER=user" ^
  -e "MINIO_ROOT_PASSWORD=password" ^
  --name minio-app ^
  quay.io/minio/minio server /data --console-address ":9001"

docker run --net=host -it --entrypoint=/bin/sh minio/mc -c ^
"^
mc config host add minio-server http://127.0.0.1:9000 user password; ^
mc mb minio-server/web-clipboard; ^
mc admin user add minio-server web-clipboard password; ^
mc admin policy set minio-server readwrite user=web-clipboard; ^
"