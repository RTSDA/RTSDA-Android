# RTSDA Church Android App

Official Android application for the Rockville-Tolland Seventh-day Adventist Church.

## Features

- Event calendar and notifications
- Sermon streaming and archives
- Church announcements
- Ministry information
- Prayer requests
- Contact directory

## Project Structure

- `app/` - Main Android application module
- `gradle/` - Gradle configuration files
- `firestore.rules` - Firebase security rules
- `secrets.properties` - API keys and sensitive configuration (not in git)

## Development Setup

1. Clone the repository
2. Copy `secrets.example.properties` to `secrets.properties` and fill in required values
3. Set up Firebase project and download `google-services.json`
4. Build and run with Android Studio

## Security

- Never commit `secrets.properties` or `google-services.json`
- Use Firebase security rules for data access control
- Follow Android security best practices
- Keep API keys and credentials secure

## License

This project uses a dual licensing approach:

### Application Source Code
The Android application source code is licensed under the GNU General Public License v3 (GPLv3). 
See the [LICENSE](LICENSE) file for details.

### Church Content
The content accessible through this application (including sermons, events, media, and other 
church-specific materials) is copyrighted by the Rockville-Tolland Seventh-day Adventist Church. 
All rights reserved. These materials are not covered by the GPL license and require proper 
authorization for use.

### Third-Party Components
This application uses various third-party components (Firebase, YouTube API, etc.) that are 
subject to their own licenses and terms of use.

### Privacy
User data collection and processing are governed by our privacy policy and applicable data 
protection laws. See the LICENSE file for more details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## Contact

For technical inquiries and contributions:
- Open an issue or pull request on GitHub
- Contact the app maintainers

For permissions regarding church content usage:
Rockville-Tolland Seventh-day Adventist Church
Administrative Office
[Church Address]
[Contact Information]
