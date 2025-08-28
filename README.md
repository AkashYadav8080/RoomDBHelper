# 📦 RoomDBHelper – Android Room Persistence Library Helper

[![](https://jitpack.io/v/AkashYadav8080/RoomDBHelper.svg)](https://jitpack.io/#AkashYadav8080/RoomDBHelper)
![Platform](https://img.shields.io/badge/platform-Android-green)
![Room Version](https://img.shields.io/badge/room-2.6.1-blue)
![Kotlin](https://img.shields.io/badge/kotlin-green)
![License](https://img.shields.io/badge/license-MIT-lightgrey)
![Min API](https://img.shields.io/badge/minAPI-21-brightgreen)

**A lightweight, production-ready Kotlin library that simplifies Android Room Database implementation by eliminating boilerplate code and standardizing database setup.**

---

## 🎯 Why RoomDBHelper?

Setting up Room Database involves repetitive boilerplate code for every project. RoomDBHelper solves this by providing:

- **Zero Boilerplate**: Generic DAO interface with all CRUD operations pre-implemented
- **Singleton Pattern**: Thread-safe database initialization out of the box  
- **Type Safety**: Fully generic implementation works with any entity
- **Clean Architecture**: Promotes separation of concerns and testability
- **Production Ready**: Used in multiple apps with 100K+ downloads

---

## ✨ Features

### 🔧 Core Components
- **`BaseDao<T>`** - Generic DAO interface with common operations (insert, update, delete, etc.)
- **`RoomDatabaseBuilder`** - Singleton database builder with thread-safe initialization
- **`BaseDatabase`** - Abstract base class for all Room databases
- **Full Kotlin Coroutines Support** - All operations are suspend functions
- **Java Interoperability** - Works seamlessly with Java projects

### 📊 Supported Operations
- Insert (single & batch)
- Update (single & batch) 
- Delete (single & batch)
- Custom queries via DAO extension
- Transaction support
- Migration handling

---

## 🚀 Quick Start

### Step 1: Add to Your Project

#### Using Gradle (Recommended)
```kotlin
// settings.gradle.kts
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

// app/build.gradle.kts
dependencies {
    implementation("com.github.AkashYadav8080:RoomDBHelper:1.0.0")
    
    // Room dependencies (required)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1") // or ksp for newer projects
}

plugins {
    id("kotlin-kapt") // Required for Room annotation processing
}
```

### Step 2: Create Your Entity
```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_name")
    val name: String,
    @ColumnInfo(name = "email_address")
    val email: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
```

### Step 3: Create Your DAO
```kotlin
@Dao
interface UserDao : BaseDao<User> {
    // Custom queries beyond basic CRUD
    @Query("SELECT * FROM users WHERE user_name LIKE :name")
    suspend fun searchUsersByName(name: String): List<User>
    
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    suspend fun getUsersOrderedByDate(): List<User>
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteById(userId: Long)
}
```

### Step 4: Create Your Database
```kotlin
@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : BaseDatabase() {
    abstract fun userDao(): UserDao
    
    companion object {
        const val DATABASE_NAME = "app_database"
    }
}
```

### Step 5: Create Repository
```kotlin
class UserRepository(context: Context) {
    private val database = RoomDatabaseBuilder.getDatabase(
        context = context,
        databaseClass = AppDatabase::class.java,
        databaseName = AppDatabase.DATABASE_NAME
    )
    
    private val userDao = database.userDao()
    
    // Basic operations (inherited from BaseDao)
    suspend fun insertUser(user: User): Long = userDao.insert(user)
    suspend fun updateUser(user: User) = userDao.update(user)
    suspend fun deleteUser(user: User) = userDao.delete(user)
    suspend fun getAllUsers(): List<User> = userDao.getAll()
    
    // Custom operations
    suspend fun searchUsers(query: String): List<User> = 
        userDao.searchUsersByName("%$query%")
        
    suspend fun getRecentUsers(): List<User> = 
        userDao.getUsersOrderedByDate()
}
```

### Step 6: Use in Your UI Layer
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        userRepository = UserRepository(this)
        
        // Example usage
        lifecycleScope.launch {
            // Insert users
            val user1 = User(name = "John Doe", email = "john@example.com")
            val user2 = User(name = "Jane Smith", email = "jane@example.com")
            
            userRepository.insertUser(user1)
            userRepository.insertUser(user2)
            
            // Fetch and display
            val allUsers = userRepository.getAllUsers()
            Log.d("RoomDB", "Total users: ${allUsers.size}")
            
            // Search functionality
            val searchResults = userRepository.searchUsers("John")
            Log.d("RoomDB", "Search results: $searchResults")
        }
    }
}
```

---

## 🏗️ Architecture

### BaseDao Interface
The `BaseDao<T>` interface provides these operations out of the box:

```kotlin
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<T>): List<Long>
    
    @Update
    suspend fun update(entity: T)
    
    @Update
    suspend fun updateAll(entities: List<T>)
    
    @Delete
    suspend fun delete(entity: T)
    
    @Delete
    suspend fun deleteAll(entities: List<T>)
    
    // Add your custom @Query methods in your DAO interface
}
```

### Database Builder
```kotlin
// Thread-safe singleton pattern
val database = RoomDatabaseBuilder.getDatabase(
    context = applicationContext,
    databaseClass = YourDatabase::class.java,
    databaseName = "your_database_name"
)
```

---

## 📚 Advanced Usage

### Database Migrations
```kotlin
@Database(
    entities = [User::class],
    version = 2, // Increment when schema changes
    exportSchema = true
)
abstract class AppDatabase : BaseDatabase() {
    abstract fun userDao(): UserDao
    
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE users ADD COLUMN phone_number TEXT")
            }
        }
    }
}

// In your repository or application class
val database = RoomDatabaseBuilder.getDatabase(
    context = context,
    databaseClass = AppDatabase::class.java,
    databaseName = "app_database"
).addMigrations(AppDatabase.MIGRATION_1_2)
```

### Multiple Entities
```kotlin
@Database(
    entities = [User::class, Post::class, Comment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : BaseDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
}
```

### Testing Support
```kotlin
@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = database.userDao()
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndGetUser() = runTest {
        val user = User(name = "Test User", email = "test@example.com")
        val id = userDao.insert(user)
        
        val allUsers = userDao.getAll()
        assertThat(allUsers).hasSize(1)
        assertThat(allUsers[0].name).isEqualTo("Test User")
    }
}
```

---

## 🔧 Configuration Options

### Custom Database Configuration
```kotlin
// With custom configuration
val database = Room.databaseBuilder(
    context.applicationContext,
    AppDatabase::class.java,
    "custom_database"
)
.fallbackToDestructiveMigration() // Use with caution
.addCallback(object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Populate initial data
    }
})
.build()
```

---

## 📱 Sample App

Check out the complete [sample application](https://github.com/AkashYadav8080/RoomDBHelper/tree/main/sample) demonstrating:
- User management with CRUD operations
- Search functionality
- RecyclerView integration
- ViewBinding usage
- Repository pattern implementation

---

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

---

## 📊 Comparison

| Feature | Without RoomDBHelper | With RoomDBHelper |
|---------|---------------------|-------------------|
| DAO Boilerplate | ~20 lines per entity | 0 lines |
| Database Setup | ~15 lines | 3 lines |
| Type Safety | Manual implementation | Built-in |
| Testing Support | Complex setup | Simple mocking |
| Maintenance | High | Low |

---

## 🛠️ Requirements

- **Minimum API Level**: 21 (Android 5.0)
- **Compile SDK**: 34+
- **Kotlin**: 1.8.0+
- **Room**: 2.6.1+

---

## 🔗 Links

- **GitHub Repository**: [RoomDBHelper](https://github.com/AkashYadav8080/RoomDBHelper)
- **Documentation**: [Wiki](https://github.com/AkashYadav8080/RoomDBHelper/wiki)
- **Issues**: [Report Bug](https://github.com/AkashYadav8080/RoomDBHelper/issues)
- **Releases**: [Changelog](https://github.com/AkashYadav8080/RoomDBHelper/releases)

---

⭐ **If this library helped you, please give it a star!** ⭐

---

<div align="center">
Made with ❤️ by <a href="https://github.com/AkashYadav8080">Akash Yadav</a>
</div>
