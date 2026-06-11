#!/usr/bin/env bash
#
# moli-server 启停脚本（Linux）
#
# 用法:
#   ./moli-server.sh start
#   ./moli-server.sh stop
#   ./moli-server.sh restart
#   ./moli-server.sh status
#
# 配置:
#   1. 复制 moli-server.env.example 到部署目录 conf/moli-server.env 并修改
#   2. 或将 moli-server.env 放在与本脚本同级目录
#   3. 或通过 MOLI_ENV_FILE=/path/to/moli-server.env 指定
#
# 推荐部署目录:
#   /opt/moli/backend/
#     ├── moli-server.jar          # 或 moli-server-1.0-SNAPSHOT.jar
#     ├── application-pro.yml      # 生产配置（勿提交 Git）
#     ├── conf/moli-server.env
#     ├── logs/
#     └── run/

set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

APP_HOME="${APP_HOME:-/opt/moli/backend}"
APP_NAME="${APP_NAME:-moli-server}"
JAR_FILE="${JAR_FILE:-}"
JAVA_CMD="${JAVA_CMD:-java}"
JAVA_OPTS="${JAVA_OPTS:--Xms512m -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai}"
SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-pro}"
SPRING_ARGS="${SPRING_ARGS:-}"
STOP_TIMEOUT="${STOP_TIMEOUT:-30}"

load_env() {
  local env_file="${MOLI_ENV_FILE:-}"
  if [[ -z "$env_file" ]]; then
    for candidate in \
      "${APP_HOME}/conf/moli-server.env" \
      "${SCRIPT_DIR}/moli-server.env" \
      "${SCRIPT_DIR}/moli-server.env.local"; do
      if [[ -f "$candidate" ]]; then
        env_file="$candidate"
        break
      fi
    done
  fi

  if [[ -n "$env_file" && -f "$env_file" ]]; then
    set -a
    # shellcheck disable=SC1090
    source "$env_file"
    set +a
    echo "[INFO] loaded env: $env_file"
  else
    echo "[WARN] env file not found, using defaults (APP_HOME=$APP_HOME)"
  fi

  APP_HOME="${APP_HOME:-/opt/moli/backend}"
  APP_NAME="${APP_NAME:-moli-server}"
  JAVA_CMD="${JAVA_CMD:-java}"
  JAVA_OPTS="${JAVA_OPTS:--Xms512m -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai}"
  SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-pro}"
  SPRING_ARGS="${SPRING_ARGS:-}"
  STOP_TIMEOUT="${STOP_TIMEOUT:-30}"
  PID_FILE="${PID_FILE:-${APP_HOME}/run/${APP_NAME}.pid}"
  LOG_DIR="${LOG_DIR:-${APP_HOME}/logs}"
  LOG_FILE="${LOG_FILE:-${LOG_DIR}/${APP_NAME}.log}"

  if [[ -n "${JAVA_HOME:-}" && -x "${JAVA_HOME}/bin/java" ]]; then
    JAVA_CMD="${JAVA_HOME}/bin/java"
  fi
}

resolve_jar() {
  if [[ -n "$JAR_FILE" ]]; then
    if [[ -f "$JAR_FILE" ]]; then
      return 0
    fi
    echo "[ERROR] JAR_FILE not found: $JAR_FILE"
    return 1
  fi

  local candidates=(
    "${APP_HOME}/moli-server.jar"
    "${APP_HOME}/${APP_NAME}.jar"
  )

  local jar
  for jar in "${candidates[@]}"; do
    if [[ -f "$jar" ]]; then
      JAR_FILE="$jar"
      return 0
    fi
  done

  local matched=()
  shopt -s nullglob
  matched=("${APP_HOME}"/moli-server-*.jar)
  shopt -u nullglob

  if ((${#matched[@]} > 0)); then
    JAR_FILE="$(ls -1t "${matched[@]}" | head -n 1)"
    return 0
  fi

  echo "[ERROR] cannot find jar under $APP_HOME"
  echo "        set JAR_FILE in moli-server.env or place moli-server.jar there"
  return 1
}

is_running() {
  local pid="$1"
  [[ -n "$pid" ]] || return 1
  kill -0 "$pid" 2>/dev/null || return 1

  if [[ -r "/proc/${pid}/cmdline" ]]; then
    local cmdline
    cmdline="$(tr '\0' ' ' < "/proc/${pid}/cmdline")"
    [[ "$cmdline" == *"${JAR_FILE}"* || "$cmdline" == *"moli-server"* ]] || return 1
  fi
  return 0
}

read_pid() {
  if [[ ! -f "$PID_FILE" ]]; then
    return 1
  fi
  local pid
  pid="$(tr -d '[:space:]' < "$PID_FILE")"
  [[ "$pid" =~ ^[0-9]+$ ]] || return 1
  echo "$pid"
}

ensure_dirs() {
  mkdir -p "$LOG_DIR" "$(dirname "$PID_FILE")"
}

start_server() {
  resolve_jar || return 1
  ensure_dirs

  local pid
  if pid="$(read_pid 2>/dev/null || true)" && is_running "$pid"; then
    echo "[INFO] ${APP_NAME} already running (pid=$pid)"
    return 0
  fi

  if [[ ! -d "$APP_HOME" ]]; then
    echo "[ERROR] APP_HOME not found: $APP_HOME"
    return 1
  fi

  echo "[INFO] starting ${APP_NAME}"
  echo "[INFO] jar: $JAR_FILE"
  echo "[INFO] profile: $SPRING_PROFILES_ACTIVE"
  echo "[INFO] log: $LOG_FILE"

  cd "$APP_HOME" || return 1

  # shellcheck disable=SC2086
  nohup "$JAVA_CMD" $JAVA_OPTS -jar "$JAR_FILE" \
    --spring.profiles.active="$SPRING_PROFILES_ACTIVE" \
    $SPRING_ARGS >>"$LOG_FILE" 2>&1 &

  local new_pid=$!
  echo "$new_pid" >"$PID_FILE"
  sleep 2

  if is_running "$new_pid"; then
    echo "[OK] ${APP_NAME} started (pid=$new_pid)"
    return 0
  fi

  echo "[ERROR] failed to start ${APP_NAME}, see log: $LOG_FILE"
  rm -f "$PID_FILE"
  return 1
}

stop_server() {
  resolve_jar || true

  local pid
  if ! pid="$(read_pid 2>/dev/null || true)"; then
    echo "[INFO] ${APP_NAME} is not running (no pid file)"
    return 0
  fi

  if ! is_running "$pid"; then
    echo "[WARN] stale pid file (pid=$pid), cleaning up"
    rm -f "$PID_FILE"
    return 0
  fi

  echo "[INFO] stopping ${APP_NAME} (pid=$pid)"
  kill -15 "$pid" 2>/dev/null || true

  local waited=0
  while ((waited < STOP_TIMEOUT)); do
    if ! is_running "$pid"; then
      rm -f "$PID_FILE"
      echo "[OK] ${APP_NAME} stopped"
      return 0
    fi
    sleep 1
    waited=$((waited + 1))
  done

  echo "[WARN] graceful stop timeout, sending SIGKILL"
  kill -9 "$pid" 2>/dev/null || true
  rm -f "$PID_FILE"
  echo "[OK] ${APP_NAME} force stopped"
}

status_server() {
  resolve_jar || return 1

  local pid
  if pid="$(read_pid 2>/dev/null || true)" && is_running "$pid"; then
    echo "[OK] ${APP_NAME} is running (pid=$pid)"
    echo "     jar: $JAR_FILE"
    echo "     log: $LOG_FILE"
    return 0
  fi

  echo "[STOPPED] ${APP_NAME} is not running"
  if [[ -f "$PID_FILE" ]]; then
    echo "[WARN] stale pid file: $PID_FILE"
  fi
  return 1
}

usage() {
  cat <<EOF
Usage: $0 {start|stop|restart|status}

Environment:
  MOLI_ENV_FILE   optional path to env file

Examples:
  cp moli-server.env.example /opt/moli/backend/conf/moli-server.env
  chmod +x moli-server.sh
  ./moli-server.sh start
EOF
}

main() {
  local action="${1:-}"
  load_env

  case "$action" in
    start)
      start_server
      ;;
    stop)
      stop_server
      ;;
    restart)
      stop_server
      start_server
      ;;
    status)
      status_server
      ;;
    *)
      usage
      exit 1
      ;;
  esac
}

main "$@"
