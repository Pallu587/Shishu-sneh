# Database Schema

Shishu-Sneh uses an encrypted **Room Database** (via SQLCipher) to ensure baby health data remains private and secure.

## Entity Relationships

### 1. Babies (`babies`)
The core table storing infant profiles.
- `id`: Primary Key (Auto-generate)
- `name`: Baby's full name
- `dob`: Date of Birth (Timestamp)
- `gender`: Boy/Girl
- `bloodGroup`: Blood type
- `birthWeight`: Weight at birth
- `userId`: Link to the parent account

### 2. Vaccines (`vaccines`)
Stores the immunization schedule.
- `id`: Primary Key
- `babyId`: Foreign Key -> `babies.id`
- `name`: Vaccine name (e.g., BCG, OPV)
- `disease`: Disease it prevents
- `scheduledDate`: Expected date for the shot
- `status`: Upcoming, Due Today, Done, Overdue

### 3. Growth Entries (`growth_entries`)
Tracks metrics over time.
- `id`: Primary Key
- `babyId`: Foreign Key -> `babies.id`
- `weight`: Weight in kg
- `height`: Height in cm
- `timestamp`: Date of entry

### 4. Milestones (`milestones`)
Developmental tracking.
- `id`: Primary Key
- `babyId`: Foreign Key -> `babies.id`
- `month`: Milestone month (1-12)
- `description`: Milestone detail (e.g., "Holds head up")
- `status`: Yes/No

### 5. Feeding Logs (`feeding_logs`)
Daily activity tracking.
- `id`: Primary Key
- `babyId`: Foreign Key -> `babies.id`
- `type`: Breastmilk/Formula/Solids
- `startTime`: Timestamp
- `amount`: Amount in ml (if applicable)

## Security
Encryption is handled at the database level using **SQLCipher**. A unique passkey is generated per device to prevent unauthorized access to the database file itself.
