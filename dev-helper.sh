#!/bin/bash

# Fitness In - Development Helper Script
echo "🏋️ Aplikasi Kebugaran Inti - Development Helper"
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check current mode
DEMO_MODE=$(grep "const val DEMO_MODE = " app/src/main/java/com/example/fitnessin/util/Constants.kt | grep -o "true\|false")

echo -e "${BLUE}Current Mode:${NC} $DEMO_MODE"
echo ""

# Function to toggle demo mode
toggle_demo_mode() {
    if [ "$DEMO_MODE" = "true" ]; then
        echo -e "${YELLOW}Switching to Production Mode...${NC}"
        sed -i '' 's/const val DEMO_MODE = true/const val DEMO_MODE = false/' app/src/main/java/com/example/fitnessin/util/Constants.kt
        echo -e "${GREEN}✅ Production Mode activated${NC}"
        echo -e "${RED}⚠️ Make sure Firebase is configured!${NC}"
    else
        echo -e "${YELLOW}Switching to Demo Mode...${NC}"
        sed -i '' 's/const val DEMO_MODE = false/const val DEMO_MODE = true/' app/src/main/java/com/example/fitnessin/util/Constants.kt
        echo -e "${GREEN}✅ Demo Mode activated${NC}"
        echo -e "${BLUE}ℹ️ You can test without Firebase setup${NC}"
    fi
}

# Function to build app
build_app() {
    echo -e "${YELLOW}Building application...${NC}"
    ./gradlew assembleDebug
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Build successful!${NC}"
        echo -e "${BLUE}APK location: app/build/outputs/apk/debug/app-debug.apk${NC}"
    else
        echo -e "${RED}❌ Build failed!${NC}"
    fi
}

# Function to run tests
run_tests() {
    echo -e "${YELLOW}Running unit tests...${NC}"
    ./gradlew test --console=plain
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ All tests passed!${NC}"
    else
        echo -e "${RED}❌ Some tests failed!${NC}"
    fi
}

# Function to clean project
clean_project() {
    echo -e "${YELLOW}Cleaning project...${NC}"
    ./gradlew clean
    echo -e "${GREEN}✅ Project cleaned${NC}"
}

# Function to check Firebase config
check_firebase() {
    if [ -f "app/google-services.json" ]; then
        PROJECT_ID=$(grep "project_id" app/google-services.json | cut -d'"' -f4)
        if [ "$PROJECT_ID" = "fitness-in-demo" ]; then
            echo -e "${RED}⚠️ Using demo Firebase configuration${NC}"
            echo -e "${YELLOW}For production, replace with real google-services.json${NC}"
        else
            echo -e "${GREEN}✅ Production Firebase configuration detected${NC}"
            echo -e "${BLUE}Project ID: $PROJECT_ID${NC}"
        fi
    else
        echo -e "${RED}❌ google-services.json not found!${NC}"
    fi
}

# Function to show status
show_status() {
    echo -e "${BLUE}=== Application Status ===${NC}"
    echo -e "Mode: ${YELLOW}$DEMO_MODE${NC}"
    check_firebase
    echo ""
    
    if [ "$DEMO_MODE" = "true" ]; then
        echo -e "${GREEN}✅ Ready to run in demo mode${NC}"
        echo -e "${BLUE}• No Firebase setup required${NC}"
        echo -e "${BLUE}• Mock authentication works${NC}"
        echo -e "${BLUE}• Simulated data operations${NC}"
    else
        echo -e "${YELLOW}⚠️ Production mode - Firebase required${NC}"
        echo -e "${BLUE}• Real authentication needed${NC}"
        echo -e "${BLUE}• Database operations are real${NC}"
        echo -e "${BLUE}• Check SETUP_PRODUCTION_MODE.md${NC}"
    fi
}

# Function to install APK (if device connected)
install_apk() {
    if command -v adb &> /dev/null; then
        DEVICES=$(adb devices | grep -v "List" | grep "device" | wc -l)
        if [ $DEVICES -gt 0 ]; then
            echo -e "${YELLOW}Installing APK to connected device...${NC}"
            adb install -r app/build/outputs/apk/debug/app-debug.apk
            if [ $? -eq 0 ]; then
                echo -e "${GREEN}✅ APK installed successfully${NC}"
            else
                echo -e "${RED}❌ Installation failed${NC}"
            fi
        else
            echo -e "${RED}❌ No Android device connected${NC}"
        fi
    else
        echo -e "${RED}❌ ADB not found in PATH${NC}"
    fi
}

# Menu
while true; do
    echo ""
    echo -e "${BLUE}Choose an option:${NC}"
    echo "1. Show status"
    echo "2. Toggle demo/production mode"
    echo "3. Build application"
    echo "4. Run tests"
    echo "5. Clean project"
    echo "6. Install APK (if device connected)"
    echo "7. Open Firebase Console"
    echo "8. Open project in Android Studio"
    echo "0. Exit"
    echo ""
    read -p "Enter choice [0-8]: " choice

    case $choice in
        1)
            show_status
            ;;
        2)
            toggle_demo_mode
            ;;
        3)
            build_app
            ;;
        4)
            run_tests
            ;;
        5)
            clean_project
            ;;
        6)
            install_apk
            ;;
        7)
            echo -e "${BLUE}Opening Firebase Console...${NC}"
            open "https://console.firebase.google.com/"
            ;;
        8)
            echo -e "${BLUE}Opening project in Android Studio...${NC}"
            open -a "Android Studio" .
            ;;
        0)
            echo -e "${GREEN}Goodbye! 👋${NC}"
            break
            ;;
        *)
            echo -e "${RED}Invalid option${NC}"
            ;;
    esac
done
