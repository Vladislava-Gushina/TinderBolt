package com.javarush.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "ai_vladislava_bot"; //TODO: добавь имя бота в кавычках
    public static final String TELEGRAM_BOT_TOKEN = "6500722110:AAGz1jca0_x44OWOCmlIQ0Vp6PCMM0O4iLI"; //TODO: добавь токен бота в кавычках
    public static final String OPEN_AI_TOKEN = "sk-proj-Wl8FRjGFuwSAFRww81xmT3BlbkFJt6KL5gpHkWYlv7BYRB3B"; //TODO: добавь токен ChatGPT в кавычках

    public String sms;
    private ChatGPTService chatGPT = new ChatGPTService(OPEN_AI_TOKEN);
    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }
    private DialogMode currentMode = null;
    private ArrayList<String> list = new ArrayList<>();

    private UserInfo me;
    private UserInfo she;
    private int questionCount;

    @Override
    public void onUpdateEventReceived(Update update) {
        CallbackQuery callbackQuery = null;
        if (update.hasCallbackQuery()) {
            callbackQuery = update.getCallbackQuery();
        }

        String data = callbackQuery != null ? callbackQuery.getData() : null;

        if ("start".equals(data)) {
            sendTextButtonsMessage("Выберите какая йога вам интересна:","Похудение", "skinny", "Спокойствие и гармония", "calmness");
            return;
        }
        if ("skinny".equals(data)){
            sendTextMessage("Вот что я нашла:\n1.*Уттанасана (наклон к стопам)*. Данное упражнение обеспечивает активизацию работы мышц брюшной полости, в результате чего подтягивается нижняя часть живота, «уходят» бока. Для начинающих это упражнение довольно сложное, поэтому выполнять его нужно с постепенным усложнением, нагибаясь каждый раз все ниже. Главное условие — не сгибать колени.\n" +
                    "2.*Сарвангасана*. Знакомая всем еще из школьного спортзала «Березка». Эта асана заставляет работать мышцы пресса, положительно влияет на пищеварительную систему.\n" +
                    "3.*Парипурна Навасана (поза лодки)*. Это упражнение задействует и нижнюю, и верхнюю часть пресса, а помимо того укрепляет мышцы спины и бедер.\n" +
                    "4.*Скрутка*. Данное упражнение растягивает и тренирует косые мышцы живота. С его помощью убирается жир с боков, кроме того, оно благотворно воздействует на позвоночник.");
            sendPhotoMessage("yoga");
            return;
        } else if ("calmness".equals(data)) {
            sendTextMessage("Вот что я нашла:\n1. *Баласана (поза Ребенка):* наклонитесь и опустите колени на пол. Соедините большие пальцы ног и отведите таз к пяткам. Живот должен оказаться между бёдер, а грудная клетка между колен. Перебирая пальцами рук, потянитесь ими в противоположную сторону от таза. Задача, увеличить расстояние между ладонями и тазом. И сократить расстояние от ягодиц до пяток. Усилие делаем на выдох. Задерживаемся примерно на пять дыхательных циклов, а можно и больше. Помогает снять напряжение с поясничного отдела, расслабить и комфортно потянуть плечи. (Если появляется боль в коленном суставе, не уводите таз слишком вниз). Восстановить дыхание и поясницу.\n" +
                    "2. *Ананда Баласана (поза счастливого Ребенка):* ложимся на спину и поднимаем ноги. Сгибаем колени и направляем их к подмышечным впадинам. Ладонями перехватываем пятки с внутренней стороны и нажимаем на свои стопы. Внутреннюю поверхность бедер к бокам. Пальцы ног в сторону коврика. Поясницу делаем плоской максимально прижимаем к полу. Если этого не происходит, чуть напрягаем мышцы живота и подкручиваем таз. Голова остаётся на коврике. Фиксируем положение на 5-10 дыхательных циклов.\n");
            sendPhotoMessage("yoga");
            return;
        }

        if ("stop".equals(data)) {
            System.out.println("Я буду ждать вас вновь! Для большего функционала нажмите на /start");
            sendTextMessage("Ожидаю нашей встречи!");
            return;
        }

        String sms = getMessageText();
        if (sms.equals("/start")) {
            currentMode = DialogMode.MAIN;
            sendPhotoMessage("main");
            String text = loadMessage("main");
            sendTextMessage(text);

            showMainMenu("Начало", "/start",
                    "генерация Tinder-профля \uD83D\uDE0E", "/profile",
                    "сообщение для знакомства \uD83E\uDD70", "/opener",
                    "переписка от вашего имени \uD83D\uDE08", "/message",
                    "переписка со звездами \uD83D\uDD25", "/date",
                    "задать вопрос чату GPT \uD83E\uDDE0", "/gpt");
            return;
        }

        if(sms.equals("/gpt")){
            currentMode = DialogMode.GPT;
            sendPhotoMessage("gpt");
            String text = loadMessage("gpt");
            sendTextMessage(text);
            return;
        }

        if(currentMode == DialogMode.GPT && !isMessageCommand()){
            String promnt = loadPrompt("gpt");
            Message msg = sendPhotoMessage("Подождите пару секунд - ChatGPT думает...");
            String answer = chatGPT.sendMessage(promnt, sms);
            updateTextMessage(msg, answer);
            return;
        }

        if(sms.equals("/date")){
            currentMode = DialogMode.DATE;
            sendPhotoMessage("date");
            String text = loadMessage("date");
            sendTextButtonsMessage(text,
                     "Ариана Гранде", "date_grande",
                    "Марго Робби", "date_robbie",
                    "Зендея", "date_zendaya",
                    "Райн Гослинг", "date_gosling",
                    "Том харди", "date_hardy");
            return;
        }

        if (currentMode==DialogMode.DATE && !isMessageCommand()){
            String query = getCallbackQueryButtonKey();
            if(query.startsWith("date_")){
                sendPhotoMessage(query);
                sendTextMessage("Отличный выбор! \nТвоя задача пригласить партнера на свидание ❤\uFE0F за 5 сообщений");

                String prompt = loadPrompt(query);
                chatGPT.setPrompt(prompt);
                return;
            }

            Message msg = sendPhotoMessage("Подождите пару секунд - партнер думает...");
            String answer = chatGPT.addMessage(sms);
            updateTextMessage(msg, answer);
            return;
        }

            if (sms.equals("/message")){
                currentMode = DialogMode.MESSAGE;
                sendPhotoMessage("message");
                sendTextButtonsMessage("Пришлите в чат вашу переписку",
                        "Следующее сообщение", "message_text",
                        "Пригласить на свидание", "message_date");
                return;
            }

            if(currentMode == DialogMode.MESSAGE && !isMessageCommand()){
                String query = getCallbackQueryButtonKey();
                if(query.startsWith("message_")){
                   String prompt = loadPrompt(query);
                   String userChatHistory = String.join("\n\n", list);

                   Message msg = sendTextMessage("Подождите пару секунд - ChatGPT думает...");
                   String answer = chatGPT.sendMessage(prompt, userChatHistory);
                   updateTextMessage(msg, answer);
                   return;
                }

                list.add(sms);
                return;
            }

        if(sms.equals("/profile"))  {
            currentMode = DialogMode.PROFILE;
            sendPhotoMessage("profile");

            me = new UserInfo();
            questionCount = 1;
            sendTextMessage("Сколько вам лет?");
            return;
        }

        if(currentMode == DialogMode.PROFILE && !isMessageCommand()){
            switch (questionCount){
                case 1:
                    me.age = sms;

                    questionCount = 2;
                    sendTextMessage("Кем вы работаете?");
                    return;
                case 2:
                    me.occupation = sms;

                    questionCount = 3;
                    sendTextMessage("Есть ли у вас хобби?");
                    return;
                case 3:
                    me.occupation = sms;
                    questionCount = 4;
                    sendTextMessage("Что вам НЕ нравится в людях?");
                    return;

                case 4:
                    me.annoys = sms;

                    questionCount = 5;
                    sendTextMessage("Что вы ожидание от свидания?");
                    return;

                case 5:
                    me.goals= sms;

                    String aboutMyself = me.toString();
                    String prompt = loadPrompt("profile");

                    Message msg = sendTextMessage("Подождите пару секунд - ChatGPT думает...");
                    String answer = chatGPT.sendMessage(prompt, aboutMyself);
                    updateTextMessage(msg, answer);
                    return;
            }

            return;
        }

        if (sms.equals("/opener")){
            currentMode = DialogMode.OPENER;
            sendPhotoMessage("opener");

            she = new UserInfo();
            questionCount = 1;
            sendTextMessage("Имя парнера:");
            return;
        }

        if(currentMode == DialogMode.OPENER && !isMessageCommand()){
            switch (questionCount){
                case 1:
                    she.name = sms;
                    questionCount = 2;
                    sendTextMessage("Сколько ей/ему лет?");
                    return;
                case 2:
                    she.age = sms;
                    questionCount = 3;
                    sendTextMessage("Есть ли у неё/него хобби?");
                    return;
                case 3:
                    she.hobby = sms;
                    questionCount = 4;
                    sendTextMessage("Кем она/он работает?");
                    return;
                case 4:
                    she.occupation = sms;
                    questionCount = 5;
                    sendTextMessage("Цель знакомства?");
                    return;
                case 5:
                    she.goals = sms;
                    String aboutFriend = sms;
                    String prompt = loadPrompt("opener");

                    Message msg = sendTextMessage("Подождите пару секунд - ChatGPT думает...");
                    String answer = chatGPT.sendMessage(prompt, aboutFriend);
                    updateTextMessage(msg, answer);
                    return;
            }

            return;
        }


        sendTextMessage("Привет! Я современный искуственный интелект Владислава");
        sendTextMessage("Нажмите на /start, чтобы узнать все мои возможности");
        sendTextButtonsMessage("Моя дополнительная возможность: Йога и медитация",
                "Начать!", "start", "Нужно подумать", "stop");

    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
