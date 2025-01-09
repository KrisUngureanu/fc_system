import {DataChecker, Util} from './util.js';

export class Autocomplete {
	
	static autocomplete(inp) {
		if(!inp) return false;
	
		new AutoInput(inp);
	}
	
}

class AutoInput {
	static heightRange = 40;

	constructor(input) {
		this.inp = input;
		this.currentFocus = 0;
		this.init();
	}
	
	init() {
		let _this = this;
	
		this.inp.addEventListener("input", function(e){
			/*
				не показывать поиск по одной букве
				if(val.length < 2){
					_this.closeAllLists();
					return false;
			} */
			_this.getUserDeals(this.value);
		});
		
		this.inp.addEventListener("keydown", function(e){ 
			var x = document.getElementById(this.id + "autocomplete-list");
			if(x) x = x.getElementsByTagName("div");
			if(e.keyCode === 40){
				_this.currentFocus++;
				_this.addActive(x);
			} else if(e.keyCode === 38){
				_this.currentFocus--;
				_this.addActive(x);
			} else if(e.keyCode === 13){
				e.preventDefault();
				if(_this.currentFocus > -1) {
					if(x) x[_this.currentFocus].click();
				}
			}
		});
		
		document.addEventListener("keydown", function(e){
			if(e.keyCode === 27){
				_this.closeAllLists();
			}
				
		});
		
		document.addEventListener("click", function(e) {
			_this.closeAllLists(e.target);
		});
	
	}

	getUserDeals(val) {
		let _this = this;
	
		var par = {"cmd":"getUserPrivateDeal", "text": val};
		
		Util.post2(par).then(function(json){
			DataChecker.checkData(json).then(data => {
			
				var nameArr = (data.iinNames) ? data.iinNames.substr(1).split(";") : null;
				var uidArr = (data.iinNames) ? data.uids.substr(1).split(";") : null;
				
				_this.closeAllLists();
				
				if (!val) {return false;}
				_this.currentFocus = 0;
				
				let a = document.createElement("div");
				a.setAttribute("id", _this.inp.id + "autocomplete-list");
				a.setAttribute("class", "autocomplete-items");
				
				a.style.top = $(_this.inp).offset().top + $(_this.inp).height() + 4;
				a.style.left = $(_this.inp).offset().left;
				a.style.right = $( window ).width() - $(_this.inp).offset().left - $(_this.inp).width() - 44;
				
				document.body.appendChild(a);
				
				if(!nameArr || nameArr.lengh == 0) {
					_this.closeAllLists();
					return false;}
				for(var i = 0; i<nameArr.length; i++){
					let k = nameArr[i].toUpperCase().indexOf(val.toUpperCase());
					if(k !== -1){
						let b = document.createElement("div");
						b.setAttribute("id", _this.inp.id + "autocomplete-list" + i);
						b.innerHTML = nameArr[i].substr(0, k);
						b.innerHTML += "<strong>" + nameArr[i].substr(k, val.length) + "</strong>";
						b.innerHTML += nameArr[i].substr(k + val.length);
						b.innerHTML += "<input type='hidden' value='" + nameArr[i] + "'>";
						b.innerHTML += "<input type='hidden' value='" + i + "'>";
						b.addEventListener("click", function(e){						
							var index = this.getElementsByTagName("input")[1].value;
							var uid = uidArr[index];
							_this.closeAllLists();
							Util.blockPage();
							document.location.hash = "cmd=openLDIfc&uid=" + uid;
						});
						a.appendChild(b);
					}
				}
				var x = document.getElementById(_this.inp.id + "autocomplete-list");
				if(x) x = x.getElementsByTagName("div");
				_this.addActive(x);			
			});
		});
	
	}

	addActive(x){
		if(!x) return false;
		this.removeActive(x);
		let currentFocus = this.currentFocus;
		
		if(currentFocus >= x.length) currentFocus = 0;
		if(currentFocus < 0) currentFocus = x.length - 1;
		
		x[currentFocus].classList.add("autocomplete-active");
		
		var divTop = $("#" + x[currentFocus].id).offset().top;
		var parBottom = $(".autocomplete-items").height() + $(".autocomplete-items").offset().top - this.heightRange;
		if(divTop > parBottom){
			$(".autocomplete-items").scrollTop(x[currentFocus].offsetTop - $(".autocomplete-items").height() + x[currentFocus].offsetHeight);
		}
		else if(divTop < $(".autocomplete-items").offset().top){
			$(".autocomplete-items").scrollTop(x[currentFocus].offsetTop);
		}
	}
	
	removeActive(x){
		if(!x) return false;
		for(var i = 0; i<x.length; i++){
			x[i].classList.remove("autocomplete-active");
		}
	}
	
	closeAllLists(elmnt){
		var x = document.getElementsByClassName("autocomplete-items");
		for(var i =0; i<x.length; i++){
			if(elmnt != x[i] && elmnt != this.inp){
				x[i].parentNode.removeChild(x[i]);
			}
		}
	}

}
