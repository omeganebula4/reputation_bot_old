# Discord Reputation Bot

A Discord bot written in Java that implements a comprehensive user reputation system with automatic reputation detection, admin controls, and interactive leaderboards. The bot tracks reputation across weekly, monthly, and all-time periods with automatic resets and persistent MongoDB storage.

## Features

### Reputation Tracking

* **Automatic Detection**: Monitors messages for "thank you" expressions (thanks, ty, thx, tysm, etc.) with user mentions to automatically award reputation.
* **Multiple Leaderboards**: Tracks reputation separately for weekly, monthly, and all-time periods.
* **Cooldown System**: Implements a 20-second cooldown between reputation awards to prevent spam.
* **Self-Rep Prevention**: Automatically blocks users from giving themselves reputation.
* **Bot Protection**: Prevents reputation from being given to bot accounts.
* **URL Filtering**: Strips URLs from messages before processing to prevent reputation farming through link sharing.


### Interactive Leaderboards

* **Paginated Embeds**: Displays leaderboards in interactive embeds with navigation buttons.
* **Multiple Time Periods**: View separate rankings for weekly, monthly, and all-time reputation.
* **Dropdown Selection**: Choose leaderboard type via an interactive dropdown menu.
* **10 Users Per Page**: Each leaderboard page shows 10 users with rank, reputation amount, and username.


### Member Management

* **Automatic Registration**: New members are automatically added to all reputation databases when they join.
* **Auto-Cleanup**: Members with zero reputation are removed from databases when they leave the server.
* **Username Caching**: Maintains a cache of usernames to optimize database lookups and handle username changes.


### Administrative Controls

* **Add Reputation**: Admin command to manually add reputation points to users.
* **Remove Reputation**: Admin command to manually subtract reputation points from users.
* **Set Reputation**: Admin command to set a user's reputation to an exact value.
* **Flexible Targeting**: All admin commands support mentions, user IDs, and reply contexts.
* **Batch Operations**: Admin commands can target specific leaderboards or apply changes to all leaderboards simultaneously.


### Automated Maintenance

* **Weekly Reset**: Automatically resets weekly leaderboard every 7 days.
* **Monthly Reset**: Automatically resets monthly leaderboard on the first day of each month.
* **Persistent Storage**: All reputation data is stored in MongoDB with automatic codec handling.


## Commands

### User Commands

| Command (Slash) | Command (Prefix) | Description | Usage Example |
| :-- | :-- | :-- | :-- |
| `/checkrep [member]` | `.r checkrep [user]` | Displays reputation points and rank across all leaderboards for the specified user or command author. | `/checkrep @user` or `.r checkrep 123456789012345678` |
| `/show-leaderboard` | `.r leaderboard [type]` | Shows a specific leaderboard (weekly/monthly/alltime). Without a type parameter, displays a selection menu. | `/show-leaderboard leaderboard-type:alltime` or `.r leaderboard weekly` |
| (Automatic) | `thanks @user` | Automatically awards 1 reputation point when thanking users in messages. | `thanks @user for helping me!` or `ty @user` |

### Admin Commands

| Command (Prefix) | Description | Usage Example |
| :-- | :-- | :-- |
| `.r addrep <user> <type> [amount]` | Adds reputation points (default: 1) to a user in the specified leaderboard (weekly/monthly/alltime/all). | `.r addrep @user alltime 5` |
| `.r remrep <user> <type> [amount]` | Removes reputation points (default: 1) from a user in the specified leaderboard. | `.r remrep @user monthly 3` |
| `.r setrep <user> <type> <amount>` | Sets a user's reputation to an exact value in the specified leaderboard. | `.r setrep @user weekly 10` |

**Note**: The bot uses `.r` as the command prefix. Admin commands require administrator permissions.

## Project Structure

### Core Components

**Main Entry Point**:

* `Main.java`: Initializes the JDA bot, registers all event listeners and commands, and sets up the command handler with the `.r` prefix.


### Command Handlers

**User Commands**:

* `Checkrep.java`: Handles the prefix-based `.r checkrep` command to display user reputation stats.
* `LeaderboardCmd.java`: Manages the prefix-based `.r leaderboard` command with dropdown menu for leaderboard selection.

**Admin Commands**:

* `Addrep.java`: Implements the admin command to add reputation points to users.
* `Remrep.java`: Implements the admin command to remove reputation points from users.
* `Setrep.java`: Implements the admin command to set reputation to a specific value.


### Slash Command \& Interaction Handlers

* `CheckrepSlash.java`: Implements the `/checkrep` slash command.
* `LeaderboardInteractionHandler.java`: Manages the `/show-leaderboard` slash command and handles all button click events for leaderboard pagination.


### Event Listeners

* `RepDetect.java`: Listens to all guild messages and automatically awards reputation when "thank you" expressions are detected with user mentions.
* `MemberHandler.java`: Handles member join/leave events to automatically manage database entries.
* `UsernameHandler.java`: Monitors username changes and updates the cache and database accordingly.


### UI Components

* `LeaderboardEmbedBuilder.java`: Constructs rich embed messages for leaderboard displays with formatting for rank, reputation, and usernames.
* `LeaderboardEmbedManager.java`: Manages leaderboard state and pagination for prefix commands.
* `LeaderboardEmbedManagerSlash.java`: Manages leaderboard state and pagination for slash commands.


### Data Models

* `ReputationData.java`: POJO representing a user's reputation entry with member ID, guild ID, rep amount, and rank calculation.
* `UserData.java`: POJO storing user information (user ID, username, discriminator) for the username cache.


### Data Access \& Utilities

* `ReputationDAO.java`: Data Access Object managing MongoDB connections and operations for all three reputation collections (weekly, monthly, all-time).
* `UsernameCache.java`: Provides a simple interface for searching and adding users to the username cache.
* `CommandDetectionUtil.java`: Helper class containing methods for parsing user input, validating arguments, checking permissions, and detecting command contexts.


## Technical Details

### Database Structure

The bot uses **MongoDB** for data persistence with three separate collections:

* `alltimeCollection`: Stores all-time reputation data
* `monthlyCollection`: Stores monthly reputation (resets on the 1st of each month)
* `weeklyCollection`: Stores weekly reputation (resets every 7 days)

Each collection stores `ReputationData` documents with fields for member ID, guild ID, and reputation amount.
