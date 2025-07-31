# 📦 RoomDBHelper – Android Room Persistence Library Helper

![Platform](https://img.shields.io/badge/platform-Android-green)
![Room](https://img.shields.io/badge/room-2.6.1-blue)
![Kotlin](https://img.shields.io/badge/kotlin-✅-orange)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

A lightweight Kotlin-based Room Database helper library that standardizes DB setup and eliminates boilerplate using generic `BaseDao`, `RoomDatabaseBuilder`, and `BaseDatabase`.

---
## ✨ Features

- Generic `BaseDao<T>` interface with all common operations
- Clean `RoomDatabaseBuilder` for singleton DB initialization
- Extendable `BaseDatabase` for all Room DBs
- Works with Kotlin and supports Java interop

---

## 🔧 Setup

### Step 1: Add Module Dependency

If using in same project:

```kotlin
// settings.gradle.kts
include(":roomdbhelper")

// app/build.gradle.kts
implementation(project(":roomdbhelper"))

```
### Step 2: Add Room to Your App Module

> Because entities and DAOs are in app module, you must include Room annotations.

```kotlin
dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
}

```

### Step 3: Create Your Entity, DAO and DB

> User.kt
```kotlin

@Entity
data class User(
    @PrimaryKey val id: Int,
    val name: String
)

```

> UserDao.kt

```kotlin
@Dao
interface UserDao : BaseDao<User> {
    @Query("SELECT * FROM User")
    suspend fun getAllUsers(): List<User>
}

```

> AppDatabase.kt

```kotlin

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : BaseDatabase() {
    abstract fun userDao(): UserDao
}

```

### Step 4: Access Using Repository

```kotlin

class UserRepository(context: Context) {
    private val userDao = RoomDatabaseBuilder
        .getDatabase(context, AppDatabase::class.java, "user_db")
        .userDao()

    suspend fun insertUser(user: User) = userDao.insert(user)
    suspend fun getUsers(): List<User> = userDao.getAllUsers()
}

```

### Step 5: Use in UI

```kotlin

val repo = UserRepository(context)
lifecycleScope.launch {
    repo.insertUser(User(1, "Boss"))
    val users = repo.getUsers()
    Log.d("ROOM", "Users: $users")
}

```

### License
MIT License – Use freely with credit.
