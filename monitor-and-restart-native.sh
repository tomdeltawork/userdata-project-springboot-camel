#!/bin/bash

# 設定要監控的目錄路徑
WATCH_PATH="/mnt"

# 獲取當前容器的名稱
CONTAINER_NAME=$(basename "$(cat /proc/self/cgroup | grep 'docker' | sed 's/^.*\///' | head -n 1)")

echo "正在監控路徑: $WATCH_PATH"
echo "當前容器名稱: $CONTAINER_NAME"

# 檢查是否已安裝 inotifywait 工具
if ! command -v inotifywait &> /dev/null
then
    echo "錯誤: 未安裝 inotifywait 工具。"
    exit 1
fi

# 啟動應用程式
echo "正在啟動應用程式..."
/app/app &

# 開始監控目錄內的文件變更
echo "開始監控文件變更..."
inotifywait -m -r -e modify,create,delete,move "$WATCH_PATH" | while read path action file; do
    echo "檢測到文件變更: $action $file"
    echo "正在重新啟動容器..."
    docker restart "$CONTAINER_NAME"
done
