# FMZ Code Assistant

## Overview
FMZ Code Assistant is a versatile plugin designed to enhance your coding efficiency across various IDEs, focusing on JavaScript and Python development. It offers advanced code autocomplete features and seamless file synchronization with the FMZ server.

## Features
- **Code Autocomplete:** Enhance your coding experience with intelligent autocomplete suggestions for JavaScript and Python.
- **File Synchronization:** Automatically sync your code files to the FMZ server whenever you save them with a special `fmz@token` tag.
- **Integrated Notifications:** Utilize the native notification systems of your IDE to get instant feedback on file synchronization status.

## Installation
1. Download the FMZ Code Assistant from the [jetbrains website](https://plugins.jetbrains.com/plugin/24465-fmz-code-assistant).
2. Open your IDE and navigate to the plugin installation section.
3. Install the downloaded plugin and restart your IDE.

## Quick Start
- **Enable Autocomplete:** Simply open any JavaScript or Python file. The autocomplete feature activates as you type.
- **Sync Files:** To sync files, add the `fmz@token` tag in your file. Save the file to trigger synchronization.
- **Notifications:** Notifications appear automatically to confirm the status of your uploads.

## Building from Source

If you want to build the plugin from source, follow these steps:

1. **Prerequisites:**
    - Make sure you have JDK 17 or later installed.
    - Install IntelliJ IDEA (Community or Ultimate edition).

2. **Clone the Repository:**
   ```
   git clone https://github.com/your-repo/fmz-code-assistant.git
   cd fmz-code-assistant
   ```

3. **Build the Plugin:**
    - Open a terminal in the project root directory.
    - Run the following command:
      ```
      ./gradlew build
      ```

4. **Locate the Built Plugin:**
    - After a successful build, you can find the plugin ZIP file in:
      ```
      build/distributions/
      ```

5. **Install the Plugin:**
    - In IntelliJ IDEA, go to Settings/Preferences -> Plugins -> Install Plugin from Disk.
    - Select the ZIP file from the `build/distributions/` directory.
    - Restart the IDE to activate the plugin.

## Development Setup

To set up the development environment:

1. Open the project in IntelliJ IDEA.
2. Make sure the Gradle plugin is enabled.
3. Sync the Gradle project to download all dependencies.
4. To run the plugin in a development instance, use the Gradle task:
   ```
   ./gradlew runIde
   ```

## Support
For support queries, contact us at [support@fmz.com](mailto:support@fmz.com).

## Contributing
Contributions to the FMZ Code Assistant are welcome. Please fork the repository, make your changes, and submit a pull request.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.