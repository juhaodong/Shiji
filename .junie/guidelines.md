# DailyFoodLog Development Guidelines

This document provides essential information for developers working on the DailyFoodLog project, a Kotlin Multiplatform Compose application for food tracking and nutritional analysis.

## Build & Configuration Instructions

### Prerequisites

- JDK 21 (required for all modules)
- Android Studio Arctic Fox or newer
- Xcode 14.1+ (for iOS development)
- Kotlin 2.1.0
- Gradle 8.x

### Environment Setup

1. **Memory Configuration**:
   - The project requires significant memory for builds. Gradle is configured with 8GB of memory:
   ```
   org.gradle.jvmargs=-Xmx8192M -Dkotlin.daemon.jvm.options\="-Xmx8192M"
   ```

2. **Android Configuration**:
   - Minimum SDK: 26
   - Target/Compile SDK: 35

3. **iOS Configuration**:
   - Deployment Target: iOS 14.1+
   - Standard KMM framework integration (no CocoaPods)

### Building the Project

#### Android Build

1. Import the project into Android Studio
2. Sync Gradle files
3. Run the `androidApp` configuration

#### iOS Build

1. Build the Kotlin framework:
   ```
   ./gradlew :shared:linkDebugFrameworkIosX64
   ```
   (Use `linkDebugFrameworkIosArm64` for physical devices or `linkDebugFrameworkIosSimulatorArm64` for Apple Silicon simulators)

2. Open the Xcode project in the `iosApp` directory
3. Add the generated framework from `shared/build/bin/ios/debugFramework` to your Xcode project
4. Build and run the project

### Signing Configuration

The Android app uses a keystore file (`shiji.jks`) with the following configuration:
- Keystore password: asd123456
- Key alias: key0
- Key password: asd123456

## Project Structure

### Key Modules

1. **shared**: Contains the cross-platform code
   - `commonMain`: Shared code for all platforms
   - `androidMain`: Android-specific implementations
   - `iosMain`: iOS-specific implementations

2. **androidApp**: Android application module

3. **iosApp**: iOS application module

### Architecture

The project uses a dependency injection pattern with `me.tatarka.inject` for managing components:

- `ApplicationComponent`: The main component that extends `NetModule`
- `GlobalSettingManager`: Manages application settings using property delegates
- View Models: Handle business logic and state management

## Development Information

### Code Style & Patterns

1. **Compose UI Structure**:
   - The UI is built with Compose Multiplatform
   - `AppBase.kt` contains the main application UI and navigation
   - Material 3 components are used throughout the application

2. **Settings Management**:
   - Settings are managed through property delegates (`StringPD`, `IntPD`, `BooleanPD`)
   - `GlobalSettingManager` provides access to application settings

3. **Navigation**:
   - Uses Compose Navigation for screen transitions
   - Routes are defined in the `startRoute` variable

4. **Theming**:
   - Supports dynamic theming with Material 3
   - Palette styles can be customized
   - Supports dark mode

5. **Networking**:
   - Uses Ktorfit for API communication
   - Serialization is handled with Kotlin Serialization

### Firebase Integration

The project integrates with Firebase for:
- Authentication
- Analytics
- Crashlytics
- Messaging

### Localization

- Supports multiple languages
- Uses string resources in XML format
- Has a custom task for processing string resources

### Image Handling

- Uses Coil for image loading
- Supports image picking with Peekaboo

## Common Issues & Solutions

1. **Memory Issues During Build**:
   - If you encounter OutOfMemoryError, increase Gradle memory in `gradle.properties`

2. **iOS Build Failures**:
   - Make sure the framework is properly built with the correct architecture: 
     `./gradlew :shared:linkDebugFrameworkIosX64` (or the appropriate architecture)
   - Ensure the framework is correctly added to the Xcode project

3. **Version Management**:
   - Version code is derived from the last segment of the version name in `gradle.properties`
   - Update `aaden.mobile.version.name` to change the version

## Deployment

### Android Deployment

The project includes a script for uploading to PGyer:
```
./upload-to-pgyer.sh
```

### iOS Deployment

Standard Xcode deployment process applies.
