package com.nexus.porsuk.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Porsuk Finans — Database Migration Strategy (v43 -> v47)
 *
 * Veri kayıplarını engellemek için versiyon geçişlerini aşamalı olarak yönetir.
 */
object DatabaseMigrations {

    /**
     * Migration 1 -> 40: Baseline migration for early versions.
     * TODO: Implement detailed schema changes if supporting very old app versions.
     */
    val MIGRATION_1_40 = object : Migration(1, 40) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Placeholder: Most early changes were handled via destructive migration.
            // In a real production app, we would add the major table definitions here.
        }
    }

    /**
     * Migration 40 -> 43: Economic Calendar, News Intelligence and Portfolio Engine tables.
     */
    val MIGRATION_40_43 = object : Migration(40, 43) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `engine_economic_events` (`event_id` TEXT NOT NULL, `title` TEXT NOT NULL, `country` TEXT NOT NULL, `category` TEXT NOT NULL, `impact_level` TEXT NOT NULL, PRIMARY KEY(`event_id`))")
            db.execSQL("CREATE TABLE IF NOT EXISTS `engine_earnings_calendar` (`earnings_id` TEXT NOT NULL, `symbol` TEXT NOT NULL, `company_name` TEXT NOT NULL, `report_date` TEXT NOT NULL, `eps_forecast` REAL NOT NULL, `eps_actual` REAL, PRIMARY KEY(`earnings_id`))")
            db.execSQL("CREATE TABLE IF NOT EXISTS `engine_portfolio_engine` (`portfolio_id` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, PRIMARY KEY(`portfolio_id`))")
            db.execSQL("CREATE TABLE IF NOT EXISTS `engine_portfolio_assets` (`asset_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `portfolio_id` TEXT NOT NULL, `symbol` TEXT NOT NULL, `name` TEXT NOT NULL, FOREIGN KEY(`portfolio_id`) REFERENCES `engine_portfolio_engine`(`portfolio_id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        }
    }

    /**
     * Migration 43 -> 44: Bulut senkronizasyon kuyruğu ve kullanıcı cihazları tabloları.
     */
    val MIGRATION_43_44 = object : Migration(43, 44) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `cloud_sync_queue` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `entityType` TEXT NOT NULL,
                    `action` TEXT NOT NULL,
                    `payload` TEXT NOT NULL,
                    `timestamp` INTEGER NOT NULL,
                    `retryCount` INTEGER NOT NULL,
                    `isSynced` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `user_devices` (
                    `deviceId` TEXT NOT NULL PRIMARY KEY,
                    `deviceName` TEXT NOT NULL,
                    `osVersion` TEXT NOT NULL,
                    `lastActive` INTEGER NOT NULL,
                    `isPrimary` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    /**
     * Migration 44 -> 45: Otomasyon kuralları, AI önerileri, bildirim merkezi ve ajan metrikleri.
     */
    val MIGRATION_44_45 = object : Migration(44, 45) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `automation_rules` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `title` TEXT NOT NULL,
                    `triggerType` TEXT NOT NULL,
                    `actionType` TEXT NOT NULL,
                    `isEnabled` INTEGER NOT NULL,
                    `lastTriggered` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `automation_history` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `ruleId` TEXT NOT NULL,
                    `executedAt` INTEGER NOT NULL,
                    `status` TEXT NOT NULL,
                    `details` TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `ai_automation_suggestions` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `suggestion` TEXT NOT NULL,
                    `confidence` REAL NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `isDismissed` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `notification_center` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `title` TEXT NOT NULL,
                    `message` TEXT NOT NULL,
                    `timestamp` INTEGER NOT NULL,
                    `isRead` INTEGER NOT NULL,
                    `category` TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `agent_performance_metrics` (
                    `agentId` TEXT NOT NULL PRIMARY KEY,
                    `agentName` TEXT NOT NULL,
                    `accuracyRate` REAL NOT NULL,
                    `totalTasks` INTEGER NOT NULL,
                    `successfulTasks` INTEGER NOT NULL,
                    `avgLatencyMs` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    /**
     * Migration 45 -> 46: Aracı kurum hesapları, abonelik Yetkileri, AI çalışma alanları ve temettü zekası.
     */
    val MIGRATION_45_46 = object : Migration(45, 46) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `broker_accounts` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `brokerName` TEXT NOT NULL,
                    `accountNumber` TEXT NOT NULL,
                    `balance` REAL NOT NULL,
                    `currency` TEXT NOT NULL,
                    `isConnected` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `engine_subscription_entitlements` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `tier` TEXT NOT NULL,
                    `isPro` INTEGER NOT NULL,
                    `validUntil` INTEGER NOT NULL,
                    `features` TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `ai_workspaces` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `name` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `lastModified` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `calculation_history` (
                    `id` TEXT NOT NULL PRIMARY KEY,
                    `calculationType` TEXT NOT NULL,
                    `inputSummary` TEXT NOT NULL,
                    `outputSummary` TEXT NOT NULL,
                    `timestamp` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `dividend_intelligence` (
                    `symbol` TEXT NOT NULL PRIMARY KEY,
                    `grossDividend` REAL NOT NULL,
                    `netDividend` REAL NOT NULL,
                    `exDate` TEXT NOT NULL,
                    `paymentDate` TEXT NOT NULL,
                    `yieldPercent` REAL NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    /**
     * Migration 46 -> 47: Güvenlik & Gizlilik Merkezi denetim ve aktif oturum tabloları.
     */
    val MIGRATION_46_47 = object : Migration(46, 47) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `engine_security_audit_logs` (
                    `log_id` TEXT NOT NULL PRIMARY KEY,
                    `category` TEXT NOT NULL,
                    `title` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `timestamp` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS `index_engine_security_audit_logs_category` ON `engine_security_audit_logs` (`category`)
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `engine_security_sessions` (
                    `session_id` TEXT NOT NULL PRIMARY KEY,
                    `device_name` TEXT NOT NULL,
                    `is_active` INTEGER NOT NULL,
                    `created_at` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS `index_engine_security_sessions_session_id` ON `engine_security_sessions` (`session_id`)
                """.trimIndent()
            )
        }
    }

    /**
     * Tüm tanımlı migration geçişlerinin listesi.
     */
    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_40,
        MIGRATION_40_43,
        MIGRATION_43_44,
        MIGRATION_44_45,
        MIGRATION_45_46,
        MIGRATION_46_47
    )
}
