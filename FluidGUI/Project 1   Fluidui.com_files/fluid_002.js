//generic intercom loader here
(function(){var w=window;var ic=w.Intercom;if(typeof ic==="function"){ic('reattach_activator');ic('update',intercomSettings);}else{var d=document;var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};w.Intercom=i;function l(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://static.intercomcdn.com/intercom.v1.js';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);}if(w.attachEvent){w.attachEvent('onload',l);}else{w.addEventListener('load',l,false);}};})();

fluid.module.define('fluid.intercom', function(module) {
	'use strict';

	var init,
		simulate = false,
		impressionInterval = 300000,   //intercom receives data at least every 5 mins
		impressionIntervalHandler,
		lastSettings;

	//bind editor events to specific actions in intercom
	init = function() {
		fluid.main.on('editor.loaded', module.start);
		fluid.main.on('editor.afterLogin', module.start);
		fluid.main.on('editor.afterAccountRegister', module.start);
		fluid.main.on('editor.afterLogout', module.shutdown);

		fluid.main.on('beforeShareMail', function() {
			module.updateIfNotSet('usedMailShare', true);
		});

		fluid.main.on('afterFirstLinkDraw', function() {
			module.updateIfNotSet('everUsedLink', true);
		});

		fluid.main.on('shareLinkCopy', function() {
			module.updateIfNotSet('everCopiedShareLink', true);
		});

		fluid.main.on('projectExport', function() {
			module.updateIfNotSet('everUsedExport', true);
		});

		fluid.main.on('preview', function() {
			module.updateIfNotSet('everUsedPreview', true);
		});
	};

	//called on editorLoad, login and accountRegister. Starts intercom tracking
	module.start = function() {
		//don't start module if the user never signed up
		if( storage.get(account.get('id')).email === 'New' ) return;

		var settings = module.getSettings();

		if(!lastSettings) {
			module.useIntercom('boot', settings);
		} else {
			module.useIntercom('update', settings);
		}

		lastSettings = settings;
		impressionIntervalHandler = setInterval(module.makeImpression, impressionInterval);
	};

	//called after user logs out of his account. Stops intercom tracking
	module.shutdown = function() {
		clearInterval(impressionIntervalHandler);

		module.useIntercom('shutdown');
	};

	//called on some editor events - see init function
	module.updateIfNotSet = function(setting, value) {
		if( storage.get(account.get('id')).email === 'New' ) return;
		if( !lastSettings || lastSettings[setting] !== undefined ) return;

		var settings = module.getSettings();
		settings[setting] = value;

		module.useIntercom('update', settings);

		lastSettings = settings;
	};

	//get setting object and send it to Intercom
	module.makeImpression = function() {
		if( !lastSettings) return;

		var settings = module.getSettings();
		if(settings.email) {

			module.useIntercom('update', settings);
		}
	};

	//returns setting object with all parameters tracked by Intercom
	module.getSettings = function() {
		var accountObj = storage.get(account.get('id'));
		var accountId = account.get('id');
		var lastProjectObj = storage.get(project.get('id'));
		var regDate = new Date(accountObj.createDate);
		var currDate = new Date();
		var userHash = localStorage.getItem('intercomUserHash');

		//some account were given bad registration date - timestamp in miliseconds instead seconds.
		// The code below fixes that
		if(Math.abs( regDate.getFullYear() - currDate.getFullYear() ) < 5 ) {
			regDate = ~~(accountObj.createDate / 1000);
		} else {
			regDate = accountObj.createDate;
		}

		var intercomSettings = {
			app_id: '0cfd597cd7bc0e4d52ab285777346b78434dc51b',
			user_id: accountId,
			created_at: regDate,
			uploads: accountObj.uploads.length,
			user_hash: userHash
//            widget: {
//                activator: '#IntercomDefaultWidget'
//            }
		};

		if(lastProjectObj) {
			intercomSettings.numberOfPages = lastProjectObj.pages ? lastProjectObj.pages.length : 0;
			intercomSettings.numberOfLinks = lastProjectObj.links ? lastProjectObj.links.linkCanvId.length : 0;
			if(lastProjectObj.links && lastProjectObj.links.linkCanvId.length) {
				intercomSettings.everUsedLink = true;
			}
		}

		if(lastSettings && lastSettings.usedMailShare) intercomSettings.usedMailShare = lastSettings.usedMailShare;
		if(lastSettings && lastSettings.everUsedLink) intercomSettings.everUsedLink = lastSettings.everUsedLink;
		if(lastSettings && lastSettings.everCopiedShareLink) intercomSettings.everCopiedShareLink = lastSettings.everCopiedShareLink;
		if(lastSettings && lastSettings.everUsedPreview) intercomSettings.everUsedPreview = lastSettings.everUsedPreview;
		if(lastSettings && lastSettings.everUsedExport) intercomSettings.everUsedExport = lastSettings.everUsedExport;

		if(accountObj.projectIds && accountObj.projectIds.length) {
			intercomSettings.projectCount = accountObj.projectIds.length;
		}

		if(accountObj.email && accountObj.email !== 'New') {
			intercomSettings.email = accountObj.email;
			intercomSettings.plan = accountObj.accType;
		}

		return intercomSettings;
	};

	module.useIntercom = function(cmd, settingObj) {
		if(simulate) {
			//do not remove this console.log as it is meant to simulate real Intercom usage
			console.log('@@@ simulating intercom: ', Intercom, ',', cmd, ', ', settingObj);
		} else {
			if(settingObj) Intercom(cmd, settingObj);
			else Intercom(cmd);
		}
	};

	//module will be initialised onDocumentReady on conditions below
	$(function() {
		//this enables intercom on production servers only
		if( window.location.hostname.indexOf('fluidui.com') === -1 && !simulate ) {
			return;
		}

		var settings = module.getSettings();
		if( settings.email ) {
			lastSettings = settings;

			module.useIntercom('boot', lastSettings);
		}

		init();
	});


});