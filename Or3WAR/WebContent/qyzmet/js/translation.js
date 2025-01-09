export class Translation {
	static translation = {};

	static init(lang) {
		if (lang == 'kz') {
			this.translation['ok'] = 'OK';
			this.translation['cancel'] = 'Болдырмау';
			this.translation['print'] = 'Баспаға';
			this.translation['continue'] = 'Жалғастыру';
			this.translation['continue2'] = 'Енгізуді жалғастыру';
			this.translation['save'] = 'Сақтау';
			this.translation['errors'] = 'Мәліметтерді енгізу қателері';
			this.translation['askNextStep'] = 'Мәліметтерді ары қарай өңдеуге жіберуге сенімдісіз бе?';
			this.translation['ignore'] = 'Елемеу';
			this.translation['error'] = 'Қате';
			this.translation['wait'] = 'Күте тұрыңыз...';
			this.translation['close'] = 'Жабу';
			this.translation['saving'] = 'Мәліметтерді сақтау...';
			this.translation['canceling'] = 'Өзгерiстерді болдырмау...';
			this.translation['removeProcess'] = 'Тапсырманы жоюға сенімдісіз бе?';
			this.translation['deleting'] = 'Жою...';
			this.translation['procPerformedMessage'] = 'Процедурасы орындалуда';
			this.translation['rptGenerateMessage'] = 'Есеп қалыптастырылуда';
			this.translation['ifcNotExistMessage'] = 'Интерфейс тағайындалмаған';
			this.translation['passChange'] = 'Құпия сөзді өзгерту';
			this.translation['change'] = 'Өзгерту';
		} else { 
			this.translation['ok'] = 'OK';
			this.translation['cancel'] = 'Отмена';
			this.translation['print'] = 'Печать';
			this.translation['continue'] = 'Продолжить';
			this.translation['continue2'] = 'Продолжить ввод';
			this.translation['save'] = 'Сохранить';
			this.translation['errors'] = 'Ошибки заполнения данных';
			this.translation['saving'] = 'Сохранение данных...';
			this.translation['askNextStep'] = 'Вы уверены, что хотите передать данные в дальнейшую обработку?';
			this.translation['ignore'] = 'Игнорировать';
			this.translation['error'] = 'Ошибка';
			this.translation['wait'] = 'Подождите...';
			this.translation['close'] = 'Закрыть';
			this.translation['canceling'] = 'Отмена внесенных изменений...';
			this.translation['removeProcess'] = 'Вы уверены, что хотите удалить задачу?';
			this.translation['deleting'] = 'Удаление...';
			this.translation['procPerformedMessage'] = 'Процедура выполняется';
			this.translation['rptGenerateMessage'] = 'Отчет формируется';
			this.translation['ifcNotExistMessage'] = 'Не задан интерфейс';
			this.translation['passChange'] = 'Сменя пароля';
			this.translation['change'] = 'Изменить';
		}
	}
	
	static get(key) {
		return translation[key];
	}
}