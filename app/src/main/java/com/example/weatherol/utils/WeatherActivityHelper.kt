package com.example.weatherol.utils

data class ActivityRecommendation(
    val title: String,
    val description: String,
    val tips: String
)

object WeatherActivityHelper {

    fun getRecommendation(weatherCode: Int?, temperature: Double): ActivityRecommendation {
        val code = weatherCode ?: return defaultRecommendation()

        return when {
            temperature > 35 && isClearSky(code) -> ActivityRecommendation(
                title = "🥵 高温预警",
                description = "今天温度超过35°C，注意防暑降温",
                tips = "建议减少户外活动，多喝水，避免中暑"
            )
            temperature < 0 -> ActivityRecommendation(
                title = "❄️ 寒冷天气",
                description = "今天温度低于0°C，注意保暖",
                tips = "出门戴好帽子手套，小心路面结冰"
            )

            isClearSky(code) -> when {
                temperature >= 15 && temperature <= 28 -> ActivityRecommendation(
                    title = "☀️ 黄金天气",
                    description = "温度适宜，天气晴朗",
                    tips = "非常适合跑步、骑行、爬山、野餐等户外活动"
                )
                temperature > 28 -> ActivityRecommendation(
                    title = "☀️ 注意防晒",
                    description = "天晴但有点热",
                    tips = "户外活动请注意防晒，避开中午时段"
                )
                else -> ActivityRecommendation(
                    title = "☀️ 冬日暖阳",
                    description = "阳光明媚，但有点冷",
                    tips = "适合晒晒太阳，补充维生素D"
                )
            }

            isCloudy(code) -> ActivityRecommendation(
                title = "⛅ 散步好时机",
                description = "不晒不雨，气温舒适",
                tips = "适合公园散步、逛街、拍照打卡"
            )

            isOvercast(code) -> ActivityRecommendation(
                title = "🌥️ 室内活动",
                description = "没有阳光，体感偏沉闷",
                tips = "适合逛博物馆、图书馆、咖啡厅、看展"
            )

            isFoggy(code) -> ActivityRecommendation(
                title = "😷 减少外出",
                description = "能见度低，空气质量差",
                tips = "出门戴好口罩，开车注意安全"
            )

            isLightRain(code) -> ActivityRecommendation(
                title = "🌧️ 雨中听雨",
                description = "小雨绵绵，别有一番风味",
                tips = "适合去咖啡馆听雨看书，或者撑伞散步"
            )

            isHeavyRain(code) -> ActivityRecommendation(
                title = "🏠 宅家日",
                description = "雨天路滑，出门不便",
                tips = "适合宅家看电影、追剧、睡午觉、打游戏"
            )

            isSnow(code) -> ActivityRecommendation(
                title = "⛄ 雪中乐趣",
                description = "白雪皑皑，银装素裹",
                tips = "适合堆雪人、打雪仗、拍雪景"
            )

            else -> defaultRecommendation()
        }
    }

    private fun isClearSky(code: Int) = code == 0
    private fun isCloudy(code: Int) = code in 1..3
    private fun isOvercast(code: Int) = code == 3
    private fun isFoggy(code: Int) = code == 45 || code == 48
    private fun isLightRain(code: Int) = code in 51..55
    private fun isHeavyRain(code: Int) = code in 61..65
    private fun isSnow(code: Int) = code in 71..75

    private fun defaultRecommendation() = ActivityRecommendation(
        title = "🌈 享受每一天",
        description = "无论天气如何，保持好心情",
        tips = "关注天气变化，合理安排出行计划"
    )
}