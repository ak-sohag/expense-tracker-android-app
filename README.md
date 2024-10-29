
# Expense Tracker Android App

This is an AI-powered Expense Tracker Android app designed to simplify financial management. The app lets users capture and categorize expenses by scanning transaction receipts or screenshots. It intelligently categorizes transactions as income or expense, extracts the amount, date, and category, and saves these details for easy tracking.

## Features

- **AI Receipt Scanning**: Scan receipts or screenshots to automatically categorize and log transactions.
- **Expense & Income Management**: Separate and organize income and expenses with ease.
- **Data Extraction**: Extracts key details such as amount, date, category, and tags.
- **Organized Insights**: View categorized expenses and track spending patterns.
- **Room Database Integration**: Store transaction data locally on the device.
- **Intuitive UI**: User-friendly interface with multiple fragments (Home, Category, Shopping Cart, Notification, User).

## Tech Stack

- **Programming Language**: Java
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Database
- **API Integration**: Supports integration with AI for receipt processing
- **Libraries**: RecyclerView, LiveData, ViewModel, OkHttp for caching, etc.

## Project Structure

- **Fragments**:
  - **Home Fragment**: Overview of recent transactions and insights.
  - **Category Fragment**: Categorize and filter transactions by type.
  - **Shopping Cart Fragment**: Track expenses related to purchases.
  - **Notification Fragment**: View reminders and updates.
  - **User Fragment**: Manage user settings and preferences.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/ak-sohag/expense-tracker-android-app.git
   ```
2. Open in Android Studio.
3. Build the project and run on an Android device or emulator.

## Usage

1. Capture a receipt photo or screenshot.
2. Upload the document in-app; the AI will automatically extract and categorize the transaction.
3. View your transaction history and insights on the home screen.

## Contributing

Contributions are welcome! Feel free to open issues or create pull requests.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.
