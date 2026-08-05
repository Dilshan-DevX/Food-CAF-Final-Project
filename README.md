<p align="center">
  <img src="app/src/main/ic_launcher-playstore.png" width="120" alt="Food CAF Logo"/>
</p>

<h1 align="center">🍽️ Food CAF — Complete Food Delivery App</h1>

<p align="center">
  <strong>A full-featured, production-ready food ordering & delivery Android application built with Java and Firebase.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white&style=for-the-badge" />
  <img src="https://img.shields.io/badge/Language-Java%2011-007396?logo=openjdk&logoColor=white&style=for-the-badge" />
  <img src="https://img.shields.io/badge/Backend-Firebase-FFCA28?logo=firebase&logoColor=black&style=for-the-badge" />
  <img src="https://img.shields.io/badge/Payment-PayHere-00C853?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Maps-Google%20Maps-4285F4?logo=googlemaps&logoColor=white&style=for-the-badge" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Version-1.0-green?style=for-the-badge" />
</p>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Screenshots](#-screenshots)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Firebase Collections](#-firebase-collections)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [Dependencies](#-dependencies)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact](#-contact)

---

## 🌟 Overview

**Food CAF** is a comprehensive food delivery mobile application designed for the Sri Lankan market. It provides a seamless food ordering experience with real-time chat, online payments, GPS-based delivery tracking, and a rich catalog of food items organized by categories. The app features a beautiful, modern UI with Material Design 3 components, smooth onboarding screens, and an intuitive navigation system.

> **Package:** `com.codex.foodcaf`  
> **Currency:** LKR (Sri Lankan Rupee)  
> **Developer:** CodeX

---

## ✨ Features

### 🔐 Authentication & Security
| Feature | Description |
|---------|-------------|
| **Email/Password Sign Up** | Secure registration with strong password validation (uppercase, lowercase, numbers, special characters) |
| **Email/Password Sign In** | Firebase Authentication with account status verification |
| **Forgot Password** | Password reset via Firebase Identity Toolkit REST API using OkHttp |
| **Account Suspension** | Real-time account status monitoring — auto sign-out on suspension with push notification |
| **Input Validation** | Comprehensive form validation for email, password, username patterns |

### 🏠 Home & Discovery
| Feature | Description |
|---------|-------------|
| **Dynamic Home Feed** | Grid-based product listing loaded from Firebase Firestore |
| **Category Browsing** | Horizontal scrollable category chips for quick filtering |
| **Category Detail View** | Full product listing filtered by selected category |
| **Banner System** | Admin-configurable promotional banners fetched from Firestore |
| **Product Search** | AutoComplete search bar with live suggestions from the product database |
| **Popular Products** | Horizontally scrollable "Popular" section on product detail pages |

### 🛒 Shopping & Cart
| Feature | Description |
|---------|-------------|
| **Product Detail Page** | Full product view with image slider, ratings, preparation time, description |
| **Image Slider** | ViewPager2-based product image carousel with dots indicator |
| **Dynamic Attributes** | Configurable portion sizes (Full/Half), toppings, and variants with live price updates |
| **Quantity Selector** | Increment/decrement quantity controls with real-time price calculation |
| **Add to Cart** | Save items to Firestore-backed cart with portion-based unique document IDs |
| **Buy Now** | Instant checkout flow bypassing the cart |
| **Cart Management** | View, update quantity, remove items from cart |
| **Shake to Clear** | 📱 Accelerometer sensor-based gesture — shake your phone to clear the entire cart |
| **Cart Notifications** | BroadcastReceiver-based local notifications for cart updates |

### 💳 Checkout & Payments
| Feature | Description |
|---------|-------------|
| **Order Summary** | Detailed breakdown: items, subtotal, delivery fee (LKR 100.00), total |
| **Delivery Address** | Editable address field with GPS auto-fill |
| **Google Maps Integration** | Interactive map with marker for delivery location |
| **Contact Details** | Editable name, email, and phone with validation |
| **Cash on Delivery (COD)** | Standard cash payment option |
| **PayHere Card Payment** | 💳 Integrated PayHere SDK for online card payments (Visa/MasterCard) |
| **Payment Status** | Orders marked as "Pending" (COD) or "Paid" (card) |

### 📦 Order Management
| Feature | Description |
|---------|-------------|
| **Order History** | Complete list of all past orders sorted by date (newest first) |
| **Order Status Tracking** | Visual status indicators — ✅ Delivered/Paid, ⏳ Pending, ❌ Cancelled |
| **Order Details** | Expandable item-level breakdown with prices |
| **Order Cancellation** | Cancel pending orders within a 10-minute window |
| **Repeat Order** | One-tap re-order — instantly creates a new checkout with the same items |
| **Product Images in Orders** | Fetches and displays product images for each order |

### ❤️ Favourites
| Feature | Description |
|---------|-------------|
| **Add to Favourites** | Heart button on product detail page saves to Firestore |
| **Favourites Gallery** | Grid view of all saved favourite products |
| **Remove from Favourites** | Swipe or tap to remove with real-time count update |
| **Quick View** | Navigate directly from favourites to product detail |

### 👤 Profile Management
| Feature | Description |
|---------|-------------|
| **View Profile** | Display name, email, and profile picture |
| **Edit Profile** | Update phone number, address, and profile picture |
| **Profile Picture Upload** | Gallery picker → Firebase Storage upload → Firestore URL update |
| **GPS Location** | Auto-detect and set delivery address using FusedLocationProvider |
| **Google Maps** | Interactive map showing user's location |
| **Sign Out** | Clean session termination with UI reset |

### 💬 Real-Time Chat
| Feature | Description |
|---------|-------------|
| **User-Admin Messaging** | Real-time Firestore-based chat between users and admin |
| **Message Bubbles** | Differentiated sent/received message styles |
| **Push Notifications** | In-app notifications for new admin messages (when chat is closed) |
| **Auto-Scroll** | Chat automatically scrolls to the latest message |
| **Real-Time Updates** | Firestore snapshot listeners for instant message delivery |

### 🎨 UI/UX Features
| Feature | Description |
|---------|-------------|
| **Material Design 3** | Modern Material You components and theming |
| **Splash Screen** | Branded splash with animated circular progress indicator |
| **Onboarding Screens** | 3-screen onboarding flow introducing app features |
| **Side Navigation Drawer** | Custom drawer with profile header, dynamic menu items |
| **Bottom Navigation** | 4-tab bottom bar — Home, Categories, Orders, Profile |
| **View Binding** | Type-safe view access throughout the app |
| **Dark Mode Support** | Values-night resources for dark theme compatibility |
| **Responsive Layouts** | Multi-density (hdpi to xxxhdpi) and multi-width (600dp, 1240dp) support |
| **Edge-to-Edge** | Modern immersive display support |

### 🔔 Notifications & Receivers
| Feature | Description |
|---------|-------------|
| **Chat Notifications** | New message alerts with notification channels (Android O+) |
| **Account Suspended Alert** | Push notification on account suspension |
| **Cart Update Notifications** | Local broadcast notifications for cart changes |

---

## 🛠 Tech Stack

### Core Platform
| Technology | Purpose | Version |
|------------|---------|---------|
| **Java** | Primary programming language | 11 |
| **Android SDK** | Mobile platform | API 24–36 |
| **Gradle** | Build system | Latest |
| **AndroidX** | Jetpack libraries | Latest |

### Firebase Services
| Service | Purpose |
|---------|---------|
| **Firebase Authentication** | Email/password user authentication |
| **Cloud Firestore** | NoSQL real-time database for all app data |
| **Firebase Storage** | Profile picture and media file storage |
| **Firebase Analytics** | Usage tracking and analytics |
| **Firebase BOM** | Dependency version management (v34.9.0) |

### Google Services
| Service | Purpose |
|---------|---------|
| **Google Maps SDK** | Interactive delivery location maps |
| **Fused Location Provider** | High-accuracy GPS location services |
| **Geocoder** | Reverse geocoding (coordinates → address) |
| **Google Play Services (Location)** | Location APIs |

### Payment Gateway
| Service | Purpose |
|---------|---------|
| **PayHere Android SDK** | Online card payment processing (v3.0.18) |
| **Currency** | LKR (Sri Lankan Rupee) |

### UI & Image Libraries
| Library | Purpose | Version |
|---------|---------|---------|
| **Material Design Components** | UI components & theming | Latest |
| **Glide** | Image loading, caching, circular crops | 4.16.0 |
| **Picasso** | Alternative image loading | Latest |
| **DotsIndicator** | ViewPager2 page indicators | Latest |
| **ViewBinding** | Type-safe view access | Built-in |
| **Navigation Component** | Fragment navigation | Latest |

### Networking
| Library | Purpose | Version |
|---------|---------|---------|
| **OkHttp** | HTTP client for Firebase REST API calls | 4.12.0 |
| **Gson** | JSON serialization/deserialization | 2.8.0 |

### Code Quality
| Library | Purpose | Version |
|---------|---------|---------|
| **Lombok** | Boilerplate reduction (getters, setters, builders) | 1.18.42 |
| **JUnit** | Unit testing | Latest |
| **Espresso** | UI instrumentation testing | Latest |

### Sensors & Hardware
| API | Purpose |
|-----|---------|
| **Accelerometer Sensor** | Shake-to-clear-cart gesture detection |
| **SensorManager** | Hardware sensor access |

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                 │
├──────────────┬──────────────┬────────────────────────┤
│  Activities  │  Fragments   │      Adapters          │
│              │              │                        │
│ SplashAct.   │ HomeFragment │ HomeProductAdapter     │
│ MainActivity │ CartFragment │ CartAdapter            │
│ SignInAct.   │ CheckOut...  │ CategoryAdapter        │
│ SignUpAct.   │ OrderFrag.   │ FavAdapter             │
│ ForgotPass.  │ ProfileFrag. │ OrderHistoryAdapter    │
│ Onboarding.. │ MessageFrag. │ MessageAdapter         │
│              │ FavFrag.     │ ProductSliderAdapter   │
│              │ EditProfile  │ PopularSectionAdapter  │
│              │ SingleProd.  │ CatListAdapter         │
│              │ CatListFrag. │ HomeCategoryAdapter    │
│              │ SettingsFrag.│ PopularAdapter         │
├──────────────┴──────────────┴────────────────────────┤
│                     DATA LAYER                        │
├──────────────┬──────────────┬────────────────────────┤
│    Models    │  Receivers   │    Firebase Services    │
│              │              │                        │
│ Product      │ AccountSusp. │ Auth                   │
│ User         │ CartNotif.   │ Firestore              │
│ Order        │              │ Storage                │
│ CartItem     │              │ Analytics              │
│ Category     │              │                        │
│ Banner       │              │                        │
│ Message      │              │                        │
│ FavModel     │              │                        │
└──────────────┴──────────────┴────────────────────────┘
```

---

## 📂 Project Structure

```
FoodCAF/
├── app/
│   ├── src/main/
│   │   ├── java/com/codex/foodcaf/
│   │   │   ├── activity/                    # All Activity classes
│   │   │   │   ├── SpalshActivity.java      # Splash screen with logo animation
│   │   │   │   ├── OpeningScreen1.java      # Onboarding screen 1
│   │   │   │   ├── OpeningScreen2.java      # Onboarding screen 2
│   │   │   │   ├── OpeningScreen3.java      # Onboarding screen 3
│   │   │   │   ├── SignUpActivity.java      # User registration
│   │   │   │   ├── SigninActivity.java      # User login
│   │   │   │   ├── FogotPassActivity.java   # Password reset (OkHttp + REST)
│   │   │   │   └── MainActivity.java        # Main container (drawer + bottom nav)
│   │   │   │
│   │   │   ├── fragment/                    # All Fragment classes
│   │   │   │   ├── HomeFragment.java        # Home feed with banners & products
│   │   │   │   ├── CategoryFragment.java    # Category grid browser
│   │   │   │   ├── CatListFragment.java     # Products by category
│   │   │   │   ├── SingleProductFragment.java # Product detail + attributes
│   │   │   │   ├── CartFragment.java        # Shopping cart (+ shake gesture)
│   │   │   │   ├── CheckOutFragment.java    # Checkout + maps + payment
│   │   │   │   ├── OrderFragment.java       # Order history & tracking
│   │   │   │   ├── OrderCompleteFragment.java # Order confirmation screen
│   │   │   │   ├── ProfileFragment.java     # User profile display
│   │   │   │   ├── EditProfileFragment.java # Edit profile + photo upload
│   │   │   │   ├── FavouritesFragment.java  # Favourite products gallery
│   │   │   │   ├── MessageFragment.java     # Real-time chat with admin
│   │   │   │   └── SettingsFragment.java    # App settings
│   │   │   │
│   │   │   ├── adapter/                     # RecyclerView Adapters
│   │   │   │   ├── HomeProductAdapter.java  # Home grid product cards
│   │   │   │   ├── HomeCategoryAdapter.java # Horizontal category chips
│   │   │   │   ├── CartAdapter.java         # Cart item rows
│   │   │   │   ├── CategoryAdapter.java     # Category grid items
│   │   │   │   ├── CatListAdapter.java      # Category product listing
│   │   │   │   ├── FavAdapter.java          # Favourite items grid
│   │   │   │   ├── OrderHistoryAdapter.java # Order history rows
│   │   │   │   ├── MessageAdapter.java      # Chat message bubbles
│   │   │   │   ├── PopularAdapter.java      # Popular products
│   │   │   │   ├── PopularSectionAdapter.java # Popular section carousel
│   │   │   │   └── ProductSliderAdapter.java # Product image slider
│   │   │   │
│   │   │   ├── model/                       # Data Models (Lombok @Data)
│   │   │   │   ├── Product.java             # Product with nested Attribute
│   │   │   │   ├── User.java               # User profile model
│   │   │   │   ├── Order.java              # Order with nested OrderItem & Address
│   │   │   │   ├── CartItem.java           # Shopping cart item
│   │   │   │   ├── Category.java           # Food category
│   │   │   │   ├── Banner.java             # Promotional banner
│   │   │   │   ├── Message.java            # Chat message
│   │   │   │   └── FavModel.java           # Favourite item reference
│   │   │   │
│   │   │   └── receiver/                    # Broadcast Receivers
│   │   │       ├── AccountSuspendedReceiver.java  # Account suspension alerts
│   │   │       └── CartNotificationReceiver.java  # Cart update notifications
│   │   │
│   │   ├── res/
│   │   │   ├── layout/                      # 34 XML layouts
│   │   │   ├── drawable/                    # Icons, shapes, backgrounds
│   │   │   ├── menu/                        # Navigation menus
│   │   │   ├── navigation/                  # Nav graphs
│   │   │   ├── values/                      # Colors, strings, themes
│   │   │   ├── values-night/                # Dark mode overrides
│   │   │   ├── values-land/                 # Landscape resources
│   │   │   ├── values-w600dp/               # Tablet (medium) resources
│   │   │   ├── values-w1240dp/              # Tablet (large) resources
│   │   │   └── mipmap-*/                    # App launcher icons (all densities)
│   │   │
│   │   └── AndroidManifest.xml              # App manifest
│   │
│   ├── build.gradle                         # Module-level build config
│   └── google-services.json                 # Firebase config
│
├── build.gradle                             # Project-level build config
├── settings.gradle                          # Gradle settings
├── gradle.properties                        # Gradle JVM & AndroidX config
└── README.md                                # This file
```

---

## 🗄 Firebase Collections

```
Firestore Database
├── users/                          # User profiles
│   └── {uid}/
│       ├── name, email, address, mobileNum, profilePicUrl, status
│       ├── cart/                   # User's shopping cart (subcollection)
│       │   └── {productId_portion}/
│       │       └── productId, productName, unitPrice, qty, productPrice, attributes[]
│       └── fav/                    # User's favourites (subcollection)
│           └── {productId}/
│               └── productId
│
├── products/                       # Food items catalog
│   └── {docId}/
│       └── productId, categoryId, foodTitle, foodDetail, foodRating,
│           foodTime, productPrice, productImage[], availability, attribute[]
│
├── categories/                     # Food categories
│   └── {docId}/
│       └── category fields (name, image, etc.)
│
├── orders/                         # All orders
│   └── {orderId}/
│       └── orderId, userId, orderDate, status, paymentMethod,
│           DeliveryAddress{}, orderItems[]
│
├── banner/                         # Promotional banners
│   └── {docId}/
│       └── banner_title, banner_body, banner_date, banner_url
│
├── chats/                          # User-admin messaging
│   └── {uid}/
│       └── messages/               # Chat messages (subcollection)
│           └── {messageId}/
│               └── senderId, receiverId, messageText, timestamp
│
Firebase Storage
└── profile_images/                 # User profile pictures
    └── {uid}.jpg
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug or later
- **JDK 11** or higher
- **Android SDK** API 24+ (Android 7.0 Nougat)
- **Google Firebase** project with Firestore, Auth, Storage enabled
- **Google Maps API Key**
- **PayHere Merchant Account** (for payment integration)

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Dilshan-DevX/Food-CAF-Final-Project.git
   cd Food-CAF-Final-Project
   ```

2. **Open in Android Studio:**
   - File → Open → Select the project directory
   - Wait for Gradle sync to complete

3. **Firebase Setup:**
   - Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
   - Enable **Authentication** (Email/Password)
   - Enable **Cloud Firestore**
   - Enable **Firebase Storage**
   - Download `google-services.json` and place it in `app/`

4. **Google Maps API Key:**
   - Get an API key from [Google Cloud Console](https://console.cloud.google.com)
   - Create `local.properties` or `secrets.properties` in the project root:
     ```properties
     MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
     ```

5. **Build and Run:**
   ```bash
   ./gradlew assembleDebug
   ```
   Or simply click ▶️ **Run** in Android Studio.

---

## ⚙️ Configuration

### PayHere Payment Gateway

The app uses PayHere SDK in **Sandbox mode** for testing. To switch to production:

1. Update `CheckOutFragment.java`:
   ```java
   req.setSandBox(false);  // Change to false for production
   req.setMerchantId("YOUR_MERCHANT_ID");
   req.setMerchantSecret("YOUR_MERCHANT_SECRET");
   ```

2. Update the notify URL to your production webhook endpoint.

### Firebase API Key

For the Forgot Password feature (OkHttp REST call), update the API key in `FogotPassActivity.java`:
```java
private static final String FIREBASE_API_KEY = "YOUR_FIREBASE_API_KEY";
```

### Admin Chat

The admin user ID is hardcoded for chat functionality:
```java
private final String ADMIN_ID = "UO6OFTZdtaRAiWUJLD5TiJIuONj2";
```
Update this to match your admin's Firebase Auth UID.

---

## 📦 Dependencies

```groovy
// AndroidX & Material
implementation libs.appcompat
implementation libs.material
implementation libs.activity
implementation libs.constraintlayout
implementation libs.navigation.fragment
implementation libs.navigation.ui

// Firebase
implementation platform('com.google.firebase:firebase-bom:34.9.0')
implementation 'com.google.firebase:firebase-analytics'
implementation 'com.google.firebase:firebase-auth'
implementation 'com.google.firebase:firebase-firestore'
implementation 'com.google.firebase:firebase-storage'

// Google Maps & Location
implementation libs.play.services.maps
implementation libs.play.services.location

// Image Loading
implementation libs.glide                    // Glide 4.16.0
implementation libs.picasso                  // Picasso

// Payment Gateway
implementation 'com.github.PayHereDevs:payhere-android-sdk:v3.0.18'

// Networking
implementation 'com.squareup.okhttp3:okhttp:4.12.0'
implementation 'com.google.code.gson:gson:2.8.0'

// Code Quality
compileOnly 'org.projectlombok:lombok:1.18.42'
annotationProcessor 'org.projectlombok:lombok:1.18.42'

// UI Components
implementation libs.dotsindicator            // ViewPager2 dots

// Notifications
implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.1.0'
```

---

## 🤝 Contributing

Contributions are welcome! Follow these steps:

1. **Fork** the repository
2. **Create** a feature branch:
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit** your changes:
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push** to the branch:
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open** a Pull Request

### Contribution Guidelines
- Follow existing code style and naming conventions
- Write meaningful commit messages
- Test on multiple screen sizes and API levels
- Update README if adding new features

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 📬 Contact

<p align="center">
  <strong>Developed with ❤️ by CodeX</strong>
</p>

<p align="center">
  <a href="https://github.com/Dilshan-DevX">
    <img src="https://img.shields.io/badge/GitHub-Dilshan--DevX-181717?logo=github&style=for-the-badge" />
  </a>
</p>

---

<p align="center">
  <sub>⭐ If you found this project useful, please give it a star! ⭐</sub>
</p>
