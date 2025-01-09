import {DataChecker, Util} from './util.js';
import {Translation} from './translation.js';

export class PersonalPhoto {

    constructor(app) {
        this.app = app;
    }
	
    initProfile() {    
        let par = {"objId":this.app.userId,"attr":'аватар'};
        $.post(window.mainUrl + "&getAttr&rnd=" + Util.rnd(), par, function(res) {
            DataChecker.checkData(res).then(res1 => {
                if (res1.result) {
                    $('#my-image').attr('src', "data:image/png;base64," + res1.result);
                }
            })
        }, 'json');	
    }

    uploadYourImage() {
        let this_ = this;
        
        $('#yourUpload').fileupload({
            dropZone: $('#my-photo'),
            pasteZone: $('#my-photo'),
            url: window.mainUrl + '&width=150&height=180',
            dataType: 'json',
            done: function (e, data) {
                if (data.result.result == 'success') {
                    let par = {};
                    par["objId"] = this_.app.userId;
                    par["attr"] = 'аватар';
                    $.post(window.mainUrl + "&getAttr&rnd=" + Util.rnd(), par, function(res1) {
                        DataChecker.checkData(res1).then(res => {
                            if (res.result) {
                                this_.deleteOldImage();
                                $('#my-image').attr('src', "data:image/png;base64," + res.result);
                                $('.user-image img').attr('src', "data:image/png;base64," + res.result);
                            } else {
                                alert(Translation.translation['error'], Util.ERROR);
                            }
                        })
                    }, 'json');
                } else {
                    alert(data.result.message, Util.ERROR);
                }
            }
        }).click();
    }
    

    deleteImage() {
        let this_ = this;

        let par = {};
        par["obj"] = "USER";
        par["name"] = 'удалить фото из ЛД';
        $.post(window.mainUrl + "&sfunc&rnd=" + Util.rnd(), par, function(res1) {
            DataChecker.checkData(res1).then(res => {
                if (res.result) {
                    this_.deleteOldImage();
                    $('#my-image').attr('src', '');
                    $('.user-image img').attr('src', "css/img/empty-avatar-34.png");
                    
                } else {
                    alert(Translation.translation['error']);
                }
            })
        }, 'json');
    }

    copyImageFromData() {
        let this_ = this;

        let par = {};
        par["obj"] = "USER";
        par["name"] = 'взять фото из ЛД';
        $.post(window.mainUrl + "&sfunc&rnd=" + Util.rnd(), par, function(res1) {
            DataChecker.checkData(res1).then(res2 => {
                if (res2.result) {
                    var par = {};
                    par["objId"] = this_.app.userId;
                    par["attr"] = 'аватар';
                    $.post(window.mainUrl + "&getAttr&rnd=" + Util.rnd(), par, function(res3) {
                        DataChecker.checkData(res3).then(res4 => {
                            if (res4.result) {
                                this_.deleteOldImage();
                                $('#my-image').attr('src', "data:image/png;base64," + res4.result);
                                $('.user-image img').attr('src', "data:image/png;base64," + res4.result);
                            } else {
                                alert(Translation.translation['error']);
                            }
                        })
                    }, 'json');
                } else {
                    alert(Translation.translation['error']);
                }
            })
        }, 'json');
    }

    deleteOldImage() {
        let par = {"sfunc" : 1, "cls": "ImageUtil", "name": "deleteUserImage", "arg0": this.app.userId, "arg1": "34", "arg2": "0"};
    
        this.app.query(par).then(response => {
            if (response.status === 200) {
                response.json().then(json => {
                    DataChecker.checkData(json).then(data => {
                        if(!data) {
                            console.log("1 ошибка при вызове метода deleteUserImage в классе ImageUtil");
                        }
                    });
                });
            } else {
                console.log("2 ошибка при вызове метода deleteUserImage в классе ImageUtil");
            }
        });
    }
}