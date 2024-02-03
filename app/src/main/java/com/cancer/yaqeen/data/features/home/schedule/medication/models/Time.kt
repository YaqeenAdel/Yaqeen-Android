package com.cancer.yaqeen.data.features.home.schedule.medication.models

data class Time(
    val id: Int,
    val time: String,
    val cronExpression: String = ""
) {
    companion object {
        fun getHours24(): List<Time> {
            return listOf(
                Time(0, "00:00"),
                Time(1, "1:00"),
                Time(2, "2:00"),
                Time(3, "3:00"),
                Time(4, "4:00"),
                Time(5, "5:00"),
                Time(6, "6:00"),
                Time(7, "7:00"),
                Time(8, "8:00"),
                Time(9, "9:00"),
                Time(10, "10:00"),
                Time(11, "11:00"),
                Time(12, "12:00"),
                Time(13, "13:00"),
                Time(14, "14:00"),
                Time(15, "15:00"),
                Time(16, "16:00"),
                Time(17, "17:00"),
                Time(18, "18:00"),
                Time(19, "19:00"),
                Time(20, "20:00"),
                Time(21, "21:00"),
                Time(22, "22:00"),
                Time(23, "23:00")
            )
        }
        fun getHours12(): List<Time> {
            return listOf(
                Time(1, "01"),
                Time(2, "02"),
                Time(3, "03"),
                Time(4, "04"),
                Time(5, "05"),
                Time(6, "06"),
                Time(7, "07"),
                Time(8, "08"),
                Time(9, "09"),
                Time(10, "10"),
                Time(11, "11"),
                Time(12, "12")
            )
        }
        fun getMinutes(): List<Time> {
            return listOf(
                Time(0, "00"),
                Time(1, "01"),
                Time(2, "02"),
                Time(3, "03"),
                Time(4, "04"),
                Time(5, "05"),
                Time(6, "06"),
                Time(7, "07"),
                Time(8, "08"),
                Time(9, "09"),
                Time(10, "10"),
                Time(11, "11"),
                Time(12, "12"),
                Time(13, "13"),
                Time(14, "14"),
                Time(15, "15"),
                Time(16, "16"),
                Time(17, "17"),
                Time(18, "18"),
                Time(19, "19"),
                Time(20, "20"),
                Time(21, "21"),
                Time(22, "22"),
                Time(23, "23"),
                Time(24, "24"),
                Time(25, "25"),
                Time(26, "26"),
                Time(27, "27"),
                Time(28, "28"),
                Time(29, "29"),
                Time(30, "30"),
                Time(31, "31"),
                Time(32, "32"),
                Time(33, "33"),
                Time(34, "34"),
                Time(35, "35"),
                Time(36, "36"),
                Time(37, "37"),
                Time(38, "38"),
                Time(39, "39"),
                Time(40, "40"),
                Time(41, "41"),
                Time(42, "42"),
                Time(43, "43"),
                Time(44, "44"),
                Time(45, "45"),
                Time(46, "46"),
                Time(47, "47"),
                Time(48, "48"),
                Time(49, "49"),
                Time(50, "50"),
                Time(51, "51"),
                Time(52, "52"),
                Time(53, "53"),
                Time(54, "54"),
                Time(55, "55"),
                Time(56, "56"),
                Time(57, "57"),
                Time(58, "58"),
                Time(59, "59")
            )
        }
    }
}
