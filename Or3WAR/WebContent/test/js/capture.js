(function() {
	// The width and height of the captured photo. We will set the
	// width to the value defined here, but the height will be
	// calculated based on the aspect ratio of the input stream.

	var width = 180; // We will scale the photo width to this
	var height = 240; // This will be computed based on the input stream

	// |streaming| indicates whether or not we're currently streaming
	// video from the camera. Obviously, we start at false.

	var streaming = false;

	// The various HTML elements we need to configure or control. These
	// will be set by the startup() function.

	var video = null;
	var canvas = null;
	var photo = null;
	var startbutton = null;
	var deletebutton = null;

	var picCount = 3;
	var currPic = 0;
	var picList = null;
	var picData = [];

	function startup() {
		video = document.getElementById('video');
		canvas = document.getElementById('canvas');
		photo = document.getElementById('photo');
		startbutton = document.getElementById('startbutton');
		deletebutton = document.getElementById('deleteButton');

		navigator.getMedia = (navigator.getUserMedia
				|| navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia);

		navigator.getMedia({
			video : true,
			audio : false
		}, function(stream) {
			if (navigator.mozGetUserMedia) {
				video.mozSrcObject = stream;
			} else {
				var vendorURL = window.URL || window.webkitURL;
				video.src = vendorURL.createObjectURL(stream);
			}
			video.play();
		}, function(err) {
			console.log("An error occured! " + err);
		});

		video.addEventListener('canplay', function(ev) {
			if (!streaming) {
				// height = video.videoHeight / (video.videoWidth/width);

				// Firefox currently has a bug where the height can't be read
				// from
				// the video, so we will make assumptions if this happens.

				if (isNaN(height)) {
					height = width / (4 / 3);
				}

				video.setAttribute('width', width);
				video.setAttribute('height', height);
				canvas.setAttribute('width', width);
				canvas.setAttribute('height', height);
				streaming = true;
			}
		}, false);

		startbutton.addEventListener('click', function(ev) {
			takepicture();
			ev.preventDefault();
		}, false);

		deletebutton.addEventListener('click', function(ev) {
			picData[currPic] = null;
			showPicture();
			ev.preventDefault();
		}, false);
		
		picList = document.createElement("ul");
		picList.setAttribute("id", "blocks");
		picList.style.display = "block";
		picList.style.position = "relative";
		picList.style.width = "180px";
		picList.style.marginTop = "10px";
		picList.style.fontSize="12pt";
		photo.parentNode.appendChild(picList);
		for (i = 0; i < picCount; i++) {
			pic = document.createElement("li");
			pic.className = "state0";
			pic.textContent = i + 1;
			pic.addEventListener("click", createPictureSelector(i));
			picList.appendChild(pic);
		}
		picList.childNodes[currPic].className = "state2";
		
		showPicture();
	}

	// Capture a photo by fetching the current contents of the video
	// and drawing it into a canvas, then converting that to a PNG
	// format data URL. By drawing it on an offscreen canvas and then
	// drawing that to the screen, we can change its size and/or apply
	// other changes before drawing it.

	function takepicture() {
		var context = canvas.getContext('2d');
		if (width && height) {
			canvas.width = 180;
			canvas.height = height;
			var vw = 3 * video.videoHeight / 4;
			context.drawImage(video, (video.videoWidth - vw) / 2, 0, vw,
					video.videoHeight, 0, 0, 180, height);

			picData[currPic] = canvas.toDataURL('image/png');
			showPicture();
		} else {
			picData[currPic] = null;
			showPicture();
		}
	}

	function showPicture() {
		if (picData[currPic]) {
			photo.setAttribute('src', picData[currPic]);
			$('#submit').removeAttr('disabled');
			$('#picture').val(picData[currPic]);
		} else {
			var context = canvas.getContext('2d');
			context.fillStyle = "#AAA";
			context.fillRect(0, 0, canvas.width, canvas.height);
			var data = canvas.toDataURL('image/png');
			photo.setAttribute('src', data);
			$('#submit').prop('disabled', true);
			$('#picture').val("");
		}
	}

	function createPictureSelector(picNum) {
		return function() {
			for (i in picList.childNodes) {
				picList.childNodes[i].className = "state0";
			}
			currPic = picNum;
			picList.childNodes[currPic].className = "state2";
			showPicture();
		}
	}

	// Set up our event listener to run the startup process
	// once loading is complete.
	window.addEventListener('load', startup, false);
})();