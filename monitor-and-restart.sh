#!/bin/bash

# 設定要監控的目錄路徑
WATCH_PATH="/mnt"

# 獲取當前容器的短 ID
CONTAINER_ID=$(cat /etc/hostname)

# 確認是否掛載 Docker Socket
if [ ! -e /var/run/docker.sock ]; then
    echo "錯誤: Docker Socket (/var/run/docker.sock) 未掛載，無法執行 docker 命令。"
    exit 1
fi

# 使用 Docker API 獲取容器名稱
CONTAINER_NAME=$(curl --unix-socket /var/run/docker.sock -s http://localhost/containers/$CONTAINER_ID/json | jq -r .Name | sed 's/^\///')

if [ -z "$CONTAINER_NAME" ]; then
    echo "錯誤: 未能獲取容器名稱。請檢查 Docker 配置和容器 ID。"
    exit 1
fi

echo "正在監控路徑: $WATCH_PATH"
echo "當前容器名稱: $CONTAINER_NAME"

# 檢查是否已安裝 inotifywait 工具
if ! command -v inotifywait &> /dev/null; then
    echo "錯誤: 未安裝 inotifywait 工具。請安裝該工具後重試。"
    exit 1
fi

# 啟動 Spring Boot 應用
echo "正在啟動 Spring Boot 應用..."
java -jar app.jar &

# 開始監控目錄內的文件變更
echo "開始監控文件變更..."
inotifywait -m -r -e modify,create,delete,move "$WATCH_PATH" | while read path action file; do
    echo "檢測到文件變更: $action $file"
    echo "正在重新啟動容器..."
    docker restart "$CONTAINER_NAME"
done
