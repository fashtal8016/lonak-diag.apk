package com.example.data.model

data class ObdModeInfo(
    val modeNumber: Int,
    val hexCode: String,
    val title: String,
    val persianDescription: String,
    val technicalUsage: String,
    val sampleCommand: String,
    val isSupportedByConnectedEcu: Boolean = true
)

data class ObdProtocolInfo(
    val id: String,
    val standardName: String,
    val description: String,
    val pinOut: String,
    val typicalCars: String
)

object ObdStandardDefinitions {
    val modes = listOf(
        ObdModeInfo(
            modeNumber = 1,
            hexCode = "01",
            title = "Mode 01 - Live Data",
            persianDescription = "دسترسی به اطلاعات جاری، سنسورها و پارامترهای موتور در زمان واقعی",
            technicalUsage = "درخواست مقادیر آنی دور موتور، دمای آب، سرعت، سنسور اکسیژن و ...",
            sampleCommand = "01 0C (دور موتور)"
        ),
        ObdModeInfo(
            modeNumber = 2,
            hexCode = "02",
            title = "Mode 02 - Freeze Frame",
            persianDescription = "دسترسی به اطلاعات خودرو در آخرین خطای پیش آمده (فریم انجمادی)",
            technicalUsage = "خواندن شرایط عملکرد موتور در لحظه‌ای که کد خطا در ایسیو ثبت شده است",
            sampleCommand = "02 02 00 (دریافت فریم خطا)"
        ),
        ObdModeInfo(
            modeNumber = 3,
            hexCode = "03",
            title = "Mode 03 - Read Stored DTCs",
            persianDescription = "دریافت کدهای خطای تایید شده و دائم (Diagnostic Trouble Codes)",
            technicalUsage = "فراخوانی تمام خطاهای ثبت شده در حافظه دائم که باعث روشن شدن چراغ چک شده‌اند",
            sampleCommand = "03"
        ),
        ObdModeInfo(
            modeNumber = 4,
            hexCode = "04",
            title = "Mode 04 - Clear DTCs",
            persianDescription = "حذف کدهای خطا (DTCs) و خاموش کردن چراغ چک (MIL)",
            technicalUsage = "ریست کردن خطاهای ذخیره شده در حافظه موقت و دائم و پاکسازی فریم خطا",
            sampleCommand = "04"
        ),
        ObdModeInfo(
            modeNumber = 5,
            hexCode = "05",
            title = "Mode 05 - Oxygen Sensor Monitoring",
            persianDescription = "تست و مانیتورینگ سنسور اکسیژن و نسبت سوخت به هوا",
            technicalUsage = "ارزیابی ولتاژ، سرعت پاسخگویی و راندمان سنسورهای لاندا بالا و پایین",
            sampleCommand = "05 01 00"
        ),
        ObdModeInfo(
            modeNumber = 6,
            hexCode = "06",
            title = "Mode 06 - On-Board Monitoring Tests",
            persianDescription = "نظارت بر مانیتورینگ سیستم‌های پایش دوره‌ای (کاتالیست، ایواپ)",
            technicalUsage = "بررسی عملکرد سیستم‌هایی که به صورت پیوسته پایش نمی‌شوند و نتایج حد مجاز",
            sampleCommand = "06 01 00"
        ),
        ObdModeInfo(
            modeNumber = 7,
            hexCode = "07",
            title = "Mode 07 - Pending DTCs",
            persianDescription = "تاریخچه و کدهای خطای موقت و در انتظار",
            technicalUsage = "خطاهایی که در سیکل رانندگی اخیر رخ داده‌اند اما هنوز به آستانه روشن شدن چراغ چک نرسیده‌اند",
            sampleCommand = "07"
        ),
        ObdModeInfo(
            modeNumber = 8,
            hexCode = "08",
            title = "Mode 08 - Actuator / System Control",
            persianDescription = "کنترل و بررسی وضعیت عیب‌یاب خودرو و تست عملگرها",
            technicalUsage = "فرمان مستقیم به رله‌ها، انژکتور، پمپ بنزین و شیر برقی‌ها توسط دیاگ",
            sampleCommand = "08 01 00"
        ),
        ObdModeInfo(
            modeNumber = 9,
            hexCode = "09",
            title = "Mode 09 - Vehicle Information",
            persianDescription = "ارائه شناسه یکتای خودرو (VIN)، شماره شاسی و شناسنامه کالیبراسیون",
            technicalUsage = "استخراج شماره VIN، شماره نرم‌افزار، کالیبراسیون ایسیو و CVN",
            sampleCommand = "09 02 (درخواست VIN)"
        ),
        ObdModeInfo(
            modeNumber = 10,
            hexCode = "0A",
            title = "Mode 10 (0A) - Permanent DTCs",
            persianDescription = "دسترسی به کدهای خطای دائمی و بدون قابلیت پاک شدن سریع",
            technicalUsage = "خطاهایی که تا تکمیل چرخه‌های رانندگی مشخص و اطمینان از رفع عیب باقی می‌مانند",
            sampleCommand = "0A"
        )
    )

    val protocols = listOf(
        ObdProtocolInfo(
            id = "ISO 9141-2",
            standardName = "ISO 9141-2 (K-Line)",
            description = "پروتکل ارتباطی سریال ناهمگام تک سیمه K-Line برای خودروهای اروپایی و آسیایی قدیمی‌تر (10.4 kbps)",
            pinOut = "پین ۷ (K-Line) و پین ۱۵ اختیاری (L-Line)",
            typicalCars = "پژو ۴۰۵، سمند ساژم، پراید ساژم، خودروهای مدل ۲۰۰۰ تا ۲۰۰۸"
        ),
        ObdProtocolInfo(
            id = "ISO 14230",
            standardName = "ISO 14230-4 (KWP2000)",
            description = "پروتکل استاندارد کی‌ورد با دو نوع Fast Init و 5-Baud Init پرکاربرد در ایسیوهای بوش و ساژم و والئو",
            pinOut = "پین ۷ (K-Line)",
            typicalCars = "پژو ۲۰۶ فرانسه، والئو J34P، بوش ME7.4.4، زیمنس قدیم"
        ),
        ObdProtocolInfo(
            id = "ISO 15765",
            standardName = "ISO 15765-4 (CAN Bus)",
            description = "شبکه کنترلر محلی پرسرعت (CAN) با شناسه‌های ۱۱ بیتی و ۲۹ بیتی و سرعت‌های ۲۵۰ و ۵۰۰ کیلوبیت بر ثانیه",
            pinOut = "پین ۶ (CAN High) و پین ۱۴ (CAN Low)",
            typicalCars = "تارا، دنا پلاس، پژو ۲۰۷ جدید، شاهین، کوییک جدید، خودروهای مدرن"
        ),
        ObdProtocolInfo(
            id = "SAE J1850 PWM",
            standardName = "SAE J1850 PWM (41.6 kbps)",
            description = "پالس مدولاسیون عرضی مورد استفاده در خودروهای فورد و آمریکایی",
            pinOut = "پین ۲ و پین ۱۰",
            typicalCars = "Ford, Mazda قدیم"
        ),
        ObdProtocolInfo(
            id = "SAE J1850 VPW",
            standardName = "SAE J1850 VPW (10.4 kbps)",
            description = "پالس با عرض متغیر جنرال موتورز و کرایسلر",
            pinOut = "پین ۲",
            typicalCars = "GM, Chevrolet, Chrysler"
        )
    )
}
