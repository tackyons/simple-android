package org.simple.clinic.storage.migrations

import androidx.room.migration.Migration
import dagger.Module
import dagger.Provides

@Module
class RoomMigrationsModule {

  @Provides
  fun databaseMigrations(
      migration_3_4: Migration_3_4,
      migration_4_5: Migration_4_5,
      migration_5_6: Migration_5_6,
      migration_6_7: Migration_6_7,
      migration_7_8: Migration_7_8,
      migration_8_9: Migration_8_9,
      migration_9_10: Migration_9_10,
      migration_10_11: Migration_10_11,
      migration_11_12: Migration_11_12,
      migration_12_13: Migration_12_13,
      migration_13_14: Migration_13_14,
      migration_14_15: Migration_14_15,
      migration_15_16: Migration_15_16,
      migration_16_17: Migration_16_17,
      migration_17_18: Migration_17_18,
      migration_18_19: Migration_18_19,
      migration_19_20: Migration_19_20,
      migration_20_21: Migration_20_21,
      migration_21_22: Migration_21_22,
      migration_22_23: Migration_22_23,
      migration_23_24: Migration_23_24,
      migration_24_25: Migration_24_25,
      migration_25_26: Migration_25_26,
      migration_26_27: Migration_26_27,
      migration_27_28: Migration_27_28,
      migration_28_29: Migration_28_29,
      migration_29_30: Migration_29_30,
      migration_30_31: Migration_30_31,
      migration_31_32: Migration_31_32,
      migration_32_33: Migration_32_33,
      migration_33_34: Migration_33_34,
      migration_34_35: Migration_34_35,
      migration_35_36: Migration_35_36,
      migration_36_37: Migration_36_37,
      migration_37_38: Migration_37_38,
      migration_38_39: Migration_38_39,
      migration_39_40: Migration_39_40,
      migration_40_41: Migration_40_41,
      migration_41_42: Migration_41_42,
      migration_42_43: Migration_42_43,
      migration_43_44: Migration_43_44,
      migration_44_45: Migration_44_45,
      migration_45_46: Migration_45_46,
      migration_46_47: Migration_46_47,
      migration_47_48: Migration_47_48,
      migration_48_49: Migration_48_49,
      migration_49_50: Migration_49_50,
      migration_50_51: Migration_50_51,
      migration_51_52: Migration_51_52,
      migration_52_53: Migration_52_53,
      migration_53_54: Migration_53_54
  ): List<Migration> {
    return listOf(
        migration_3_4,
        migration_4_5,
        migration_5_6,
        migration_6_7,
        migration_7_8,
        migration_8_9,
        migration_9_10,
        migration_10_11,
        migration_11_12,
        migration_12_13,
        migration_13_14,
        migration_14_15,
        migration_15_16,
        migration_16_17,
        migration_17_18,
        migration_18_19,
        migration_19_20,
        migration_20_21,
        migration_21_22,
        migration_22_23,
        migration_23_24,
        migration_24_25,
        migration_25_26,
        migration_26_27,
        migration_27_28,
        migration_28_29,
        migration_29_30,
        migration_30_31,
        migration_31_32,
        migration_32_33,
        migration_33_34,
        migration_34_35,
        migration_35_36,
        migration_36_37,
        migration_37_38,
        migration_38_39,
        migration_39_40,
        migration_40_41,
        migration_41_42,
        migration_42_43,
        migration_43_44,
        migration_44_45,
        migration_45_46,
        migration_46_47,
        migration_47_48,
        migration_48_49,
        migration_49_50,
        migration_50_51,
        migration_51_52,
        migration_52_53,
        migration_53_54
    )
  }
}
