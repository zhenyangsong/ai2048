package com.game2048.model

data class Question(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

object QuestionBank {
    val questions = listOf(
        Question(1, "下列哪个不是Java的基本数据类型？", listOf("int", "boolean", "String", "double"), 2),
        Question(2, "Java中，用来继承类的关键字是？", listOf("interface", "extends", "implements", "super"), 1),
        Question(3, "ArrayList与LinkedList的主要区别是？", listOf("线程安全", "查找效率", "内存布局", "添加删除效率"), 3),
        Question(4, "下列哪个修饰符表示方法不能被重写？", listOf("static", "final", "abstract", "private"), 1),
        Question(5, "Java中，HashMap的key可以为null吗？", listOf("可以一个", "可以多个", "不可以", "只能在JDK1.8前"), 0),
        Question(6, "构造方法返回类型是？", listOf("void", "int", "无返回值", "Object"), 2),
        Question(7, "下面哪个不是Thread的状态？", listOf("NEW", "RUNNING", "WAITING", "TERMINATED"), 1),
        Question(8, "try-catch-finally中，finally块一定会执行吗？", listOf("是", "否", "只在有异常时", "只在catch捕获异常时"), 0),
        Question(9, "synchronized关键字的作用是？", listOf("标识符", "同步锁", "异常处理", "类型转换"), 1),
        Question(10, "Java中，创建线程的方式有几种？", listOf("1种", "2种", "3种", "4种"), 1),
        Question(11, "下列哪个集合是线程安全的？", listOf("ArrayList", "HashMap", "Vector", "HashSet"), 2),
        Question(12, "抽象类和接口的区别是？", listOf("无区别", "抽象类可以有实现", "接口可以有抽象方法", "都可以实例化"), 1)
    )

    fun getRandomQuestion(excludeIds: List<Int> = emptyList()): Question {
        return questions.filter { it.id !in excludeIds }.randomOrNull() ?: questions.first()
    }
}
