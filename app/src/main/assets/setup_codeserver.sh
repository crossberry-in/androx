#!/data/data/com.androx/files/usr/bin/bash

# ---------- COLORS ----------
RED='\033[1;31m'
GREEN='\033[1;32m'
YELLOW='\033[1;33m'
BLUE='\033[1;34m'
CYAN='\033[1;36m'
RESET='\033[0m'

BAR=30

progress() {
  P=$1
  F=$((P*BAR/100))
  E=$((BAR-F))
  printf "\r${CYAN}["
  printf "${GREEN}%0.s█" $(seq 1 $F)
  printf "${RED}%0.s░" $(seq 1 $E)
  printf "${CYAN}] ${YELLOW}%s%%${RESET}" "$P"
}

spinner() {
  pid=$!
  spin='⠋⠙⠹⠸⠼⠴⠦⠧⠇⠏'
  i=0
  while kill -0 $pid 2>/dev/null; do
    i=$(( (i+1) %10 ))
    printf "\r${BLUE}${spin:$i:1} ${YELLOW}Installing...${RESET}"
    sleep 0.15
  done
}

clear
echo -e "${GREEN}🚀 Androx → Ubuntu → code-server Setup${RESET}"
sleep 1

# 10%
progress 10
pkg update -y >/dev/null 2>&1
pkg upgrade -y >/dev/null 2>&1

# 25%
progress 25
pkg install proot-distro curl -y >/dev/null 2>&1

# 50%
progress 50
proot-distro install ubuntu >/dev/null 2>&1

# 70%
progress 70
proot-distro login ubuntu -- bash -c "
apt update >/dev/null 2>&1
apt install sudo curl -y >/dev/null 2>&1

echo 'root ALL=(ALL) NOPASSWD:ALL' > /etc/sudoers.d/root
chmod 440 /etc/sudoers.d/root
echo 'Defaults !authenticate' >> /etc/sudoers
"

# 80%
progress 80
echo
echo -e "${CYAN}📦 Installing code-server...${RESET}"

proot-distro login ubuntu -- bash -c "
curl -fsSL https://code-server.dev/install.sh | sh
" >/dev/null 2>&1 &
spinner

# 100%
progress 100
echo
echo -e "${GREEN}✅ Installation Completed Successfully!${RESET}"
echo
echo -e "${YELLOW}👉 Login Ubuntu:${RESET} proot-distro login ubuntu"
echo -e "${YELLOW}👉 Run code-server:${RESET} code-server --auth none --bind-addr 0.0.0.0:8080"
echo
echo -e "${CYAN}🌐 Open browser:${RESET} http://127.0.0.1:8080"

# Create auto-start script for code-server
AUTO_START_FILE="$HOME/.androx_codeserver_autostart"
cat > "$AUTO_START_FILE" << 'EOF'
#!/data/data/com.androx/files/usr/bin/bash

# Check if code-server is already running
if pgrep -f "code-server" > /dev/null; then
    echo "Code-server is already running!"
    echo "Open it from the Androx menu or navigate to http://127.0.0.1:8080"
else
    # Start code-server in background
    proot-distro login ubuntu -- bash -c "nohup code-server --auth none --bind-addr 0.0.0.0:8080 > ~/.codeserver.log 2>&1 &"
    echo "Code-server starting in background..."
    sleep 3
    echo "Code-server should now be accessible at http://127.0.0.1:8080"
fi
EOF

chmod +x "$AUTO_START_FILE"

echo -e "${GREEN}✨ Auto-start script created at: $AUTO_START_FILE${RESET}"
echo -e "${YELLOW}💡 Run it anytime with: $AUTO_START_FILE${RESET}"
