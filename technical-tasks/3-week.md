Реализовать logist-service
-к данному сервису должен иметь доступ только пользователь с ролью LOGIST

-реализовать возможность создания и просмотра заданий (по одному и с пагинацией). Задание включает в себя точку старта,
точку окончания маршрута, информацию о водителе (фамилия и имя водителя), описание груза, информацию об автомобиле (
гос.номер автомобиля, добавить в качестве нового поля в portal-service)
-реализовать возможность создания и просмотра каждого рейса и всех рейсов в рамках задания (по одному и с пагинацией). У
рейса должны быть поля - время создания, время начала (не заполняется при создании), время окончания (не заполняется при
создании)
-реализовать возможность создания событий в рейсе (рейс создан, рейс начат, рейс окончен, рейс отменен, поломка,
авария). События должны создаваться при получении сообщений в топик Kafka либо в очереди RabbitMQ. Необходимые поля -
тип события рейса, время возникновения события. При создании рейса он автоматически получает состояние "рейс создан".
-метод получения данных по рейсу должен показывать в том числе текущий статус рейса
-реализовать возможность получения точек геопозиции автомобиля в рейсе. Точки должны сохраняться при получении сообщений
в топик Kafka
-всю информацию данный сервис должен отдавать только логистам той компании, логисты которой создавали задания и рейс.