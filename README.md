Краткое руководство по использованию находится внизу документа.

Описание архитектуры и функционала ->

UUID пользователя сохраняется в файл userId.txt. В начале работы, приложение пытается достать 
id пользователя из файла, а в случае отсутствия создаст нового и присвоит уникальный id.

Очистка устаревших ссылок реализована, но не используется в консольной версии приложения. 
Подключение функционала в серверную реализацию, ровно как и старт сервера в задачу не входят, 
поэтому они просто не подключены. Уведомления в том числе.

Для простоты используется виртуальный домен "click.ru"

Сохранение сокращенных ссылок в консольной и серверной версиях не предусмотрено. 
Однако ShortLink реализует Serializable.
Функционал попросту не реализован до конца. 
Сейчас все сокр. ссылки хранятся в памяти.

ConsoleUI содержит весь пользовательский интерфейс. 
В данной реализации является GodObject`ом, что нарушает принцип единственной ответственности. 
В идеале сделать по MVP-Passive View и реализовать паттерн "Наблюдатель" 
на подобии C# event через интерфейс Consumer<Event <?>, где Event< ? > - Event< TData >{}. 
В такой реализации модель будет через событие сообщать презентеру об изменениях 
(Презентер в данном случае подписывается на эти изменения), а презентер, в свою очередь будет
вызывать соотв. методы отрисовки у UI.

TODO: ViewPresenter, Валидация URL, Обработка возможных ошибок, Реализация эндпоинта для уведомлений

Краткое руководство:
1. Запустить приложение из точки входа (src/main/java/Main.java)
2. На консоли отобразится список возможных команд.
3. Ввести "1" для создания короткой ссылки. После указать ссылку, которую надо сократить.
4. Ввести "2" для перехода по сокр. ссылке. После ввести уникальный код БЕЗ "click.ru/" 
5. -> Пример сокр. ссылки "click.ru/YuvXnMUd" необходимо ввести только "YuvXnMUd"
6. На консоли отобразится результат.
7. Сокращенная ссылка действительна 24 часа, но в рамках приложения не сереализуется, поэтому живет
до выхода из программы.