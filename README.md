# 💼 Inventory Billing System (Easy POS)

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-007396?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![H2](https://img.shields.io/badge/H2_Database-004088?style=for-the-badge&logo=h2&logoColor=white)
![License](https://img.shields.io/badge/License-Proprietary-red?style=for-the-badge)

**A comprehensive Java-based Point of Sale (POS) system designed for mobile selling shops, wholesale tech shops, and retail environments.**

[Features](#-features) • [Tech Stack](#-tech-stack) • [Installation](#-installation) • [Usage](#-usage) • [Project Structure](#-project-structure) • [Contact](#-contact--support)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Installation](#-installation)
- [Usage](#-usage)
- [Project Structure](#-project-structure)
- [Screenshots](#-screenshots)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact & Support](#-contact--support)

---

## 🎯 Overview

The **Inventory Billing System** (also known as **Easy POS**) is a full-featured desktop application built with Java Swing. It provides a complete solution for managing inventory, billing, customers, repairs, and wholesale operations. The system is optimized for businesses that require efficient point-of-sale operations with comprehensive inventory tracking and customer management capabilities.

### Key Highlights

- 🛍️ **Complete POS Solution** - Retail and wholesale billing
- 📦 **Inventory Management** - Real-time stock tracking and management
- 🔧 **Repair Management** - Comprehensive repair order tracking
- 👥 **Customer Management** - Retail and wholesale customer support
- 📊 **Reporting & Analytics** - Detailed sales and inventory reports
- 🖨️ **Invoice Generation** - PDF and thermal printer support
- 💳 **Payment Tracking** - Multiple payment methods and credit management
- 🔒 **User Management** - Role-based access control
- 📈 **Audit Logs** - Complete transaction and activity logging

---

## ✨ Features

### 🧾 Billing & Sales
- **Point of Sale (POS)** - Fast and intuitive billing interface
- **Bill Management** - Create, edit, hold, and retrieve bills
- **Partial Payments** - Support for partial payment tracking
- **Multiple Payment Methods** - Cash, card, cheque, and credit tracking
- **Barcode Scanning** - Quick item lookup using barcode scanners
- **Discount Management** - Item-level and bill-level discounts
- **Warranty Tracking** - Automatic warranty period calculation and tracking

### 📦 Inventory Management
- **Item Management** - Add, edit, and delete inventory items
- **Stock Tracking** - Real-time stock level monitoring
- **Low Stock Alerts** - Automatic notifications for low stock items
- **Price Management** - Separate retail and wholesale pricing
- **Supplier Management** - Complete supplier database
- **Stock History** - Track stock movements and adjustments
- **Barcode Generation** - Generate barcodes for items

### 🔧 Repair Management
- **Repair Orders** - Create and manage repair service orders
- **Repair Tracking** - Track repair status and progress
- **Repair Items** - Link items to repair orders
- **Repair Invoices** - Generate repair service invoices
- **Repair Audit** - Complete audit trail for repair operations
- **Repair Types** - Categorize different repair services

### 👥 Customer Management
- **Customer Database** - Comprehensive customer information management
- **Customer Types** - Separate retail and wholesale customers
- **Credit Limits** - Set and track customer credit limits
- **Purchase History** - Complete purchase history for each customer
- **Customer Analytics** - Track customer spending patterns

### 🏪 Wholesale Operations
- **Wholesale Orders** - Dedicated wholesale order management
- **Wholesale Customers** - Specialized customer management for B2B
- **Wholesale Pricing** - Separate pricing structure for wholesale
- **Cheque Management** - Track and manage wholesale cheques
- **Credit Management** - Extended credit terms for wholesale customers

### 📊 Reports & Analytics
- **Sales Reports** - Daily, monthly, and custom date range reports
- **Inventory Reports** - Stock levels, movements, and valuation
- **Customer Reports** - Customer activity and purchase analysis
- **Financial Reports** - Revenue, payments, and outstanding balances
- **Dashboard** - Real-time business metrics and insights

### 🔒 Security & Management
- **User Management** - Create and manage user accounts
- **Role-Based Access** - Control user permissions and access levels
- **Audit Logging** - Complete activity logging for accountability
- **Data Backup** - Automated and manual database backup
- **Data Restore** - Restore from backup files
- **License Management** - Software licensing and activation system

### 🖨️ Printing & Export
- **PDF Invoices** - Professional A4 invoice generation
- **Thermal Printing** - Support for thermal receipt printers
- **Invoice Templates** - Customizable invoice layouts
- **Export Options** - Export data for external analysis

---

## 🛠️ Tech Stack

### Core Technologies
- **Java** - Primary programming language
- **Java Swing** - Desktop GUI framework
- **FlatLaf** - Modern flat UI theme library
- **H2 Database** - Embedded database (default)
- **MySQL** - Optional external database support

### Key Libraries
- **iText PDF** - PDF generation and manipulation
- **EscPos** - Thermal printer support
- **ZXing (Zebra Crossing)** - Barcode generation and scanning
- **Apache Commons** - Utility libraries (IO, Lang, Collections, DBUtils)
- **JCalendar** - Date picker components
- **Gson** - JSON processing

### Development Tools
- **NetBeans IDE** - Development environment
- **Apache Ant** - Build automation
- **Git** - Version control

---

## 📥 Installation

### Prerequisites

- **Java Development Kit (JDK)** 8 or higher
- **MySQL Server** (optional, for external database)
- **NetBeans IDE** (recommended for development)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/chathuka55/easy_pos.git
   cd easy_pos
   ```

2. **Configure Database**
   - The system uses H2 embedded database by default (no setup required)
   - For MySQL, update database connection settings in configuration files

3. **Build the project**
   - Open the project in NetBeans IDE
   - Clean and Build the project (F11)
   - Or use Ant: `ant clean dist`

4. **Run the application**
   - From NetBeans: Run Project (F6)
   - From command line: `java -jar dist/InventoryBillingSystem.jar`

5. **Initial Setup**
   - Create an admin user account
   - Configure shop details
   - Set up initial inventory items

---

## 🚀 Usage

### Getting Started

1. **Launch the application**
   - Double-click `InventoryBillingSystem.jar` or run from IDE

2. **Login**
   - Use your credentials to log in
   - First-time setup: Create an admin account

3. **Navigate the Dashboard**
   - Access different modules from the main menu
   - View key metrics and quick actions

### Common Workflows

#### Creating a Sale
1. Navigate to **Billing** panel
2. Select or add a customer (optional)
3. Add items by barcode or search
4. Apply discounts if needed
5. Process payment
6. Print/email invoice

#### Managing Inventory
1. Go to **Items** panel
2. Add new items with details
3. Set retail and wholesale prices
4. Update stock levels
5. Generate barcodes

#### Processing Repairs
1. Open **Repair Management**
2. Create new repair order
3. Add repair items and services
4. Track repair status
5. Generate repair invoice

---

## 📁 Project Structure

```
InventoryBillingSystem/
│
├── src/                          # Source code
│   ├── dao/                      # Data Access Objects
│   │   ├── BaseDAO.java
│   │   ├── BillDAO.java
│   │   ├── CustomerDAO.java
│   │   ├── ItemDAO.java
│   │   └── ...
│   │
│   ├── models/                   # Data models
│   │   ├── Bill.java
│   │   ├── Customer.java
│   │   ├── Item.java
│   │   └── ...
│   │
│   ├── ui/                       # User interface
│   │   ├── BillingPanel.java
│   │   ├── CustomerPanel.java
│   │   ├── DashboardPanel.java
│   │   ├── ItemsPanel.java
│   │   ├── RepairPanel.java
│   │   ├── WholesalePanel.java
│   │   └── ...
│   │
│   ├── utils/                    # Utility classes
│   │   ├── BarcodeGenerator.java
│   │   ├── DatabaseBackupRestore.java
│   │   ├── ThemeManager.java
│   │   └── ...
│   │
│   ├── services/                 # Business logic
│   │   └── UserService.java
│   │
│   ├── db/                       # Database related
│   │   ├── ConnectionFactory.java
│   │   └── schema.sql
│   │
│   ├── licensing/                # License management
│   │   ├── LicenseManager.java
│   │   └── ...
│   │
│   └── main/                     # Entry point
│       └── Main.java
│
├── dist/                         # Distribution files
│   ├── InventoryBillingSystem.jar
│   └── lib/                      # Dependencies
│
├── build/                        # Build output
├── db/                           # Database files
├── Invoices/                     # Generated invoices
├── logs/                         # Application logs
├── build.xml                     # Ant build script
├── .gitignore                    # Git ignore rules
└── README.md                     # This file
```

---

## 📸 Screenshots

> _Screenshots will be added soon_

---

## 🤝 Contributing

This is a private and proprietary project. Contributions are not currently accepted from external developers.

---

## 📄 License

This project is **private and proprietary**. All rights reserved.

**Built with ❤️ by Chathuka Jayasekara**

---

## 📞 Contact & Support

### Project Maintainer

**Chathuka Jayasekara**

- **GitHub**: [@chathuka55](https://github.com/chathuka55)
- **LinkedIn**: [chathuka-jayasekara-013595216](https://www.linkedin.com/in/chathuka-jayasekara-013595216)
- **Instagram**: [@chathux_j](https://www.instagram.com/chathux_j)

---

<div align="center">

**Made with ❤️ by Chathuka Jayasekara**

⭐ Star this repo if you find it helpful!

</div>
