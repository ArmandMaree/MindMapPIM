module.exports = {
	'Can user log in?': function (test) {
	  test
	  	.open('https://accounts.google.com/login')
	    .type('#Email','acubencos@gmail.com')
	    .assert.val('#Email', 'acubencos@gmail.com', 'Entered in users email address.')
	    .click('#next')
	    .wait(2000)
	    .type('#Passwd','acuben1234')
	    .assert.val('#Passwd', 'acuben1234', 'Entered in users password.')
	    .click('#signIn')
	    .wait(10000)
    	.open("https://unclutter.iminsys.com/login")
    	.click('#login')
	    .wait(2000)
	    .assert.title().is('Register', 'Webpage has the correct title.')
	    .wait(10000)
	    .click('#googleLogin')
		.wait(10000)
	    .assert.text('#welcome','Welcome, Acuben Cos',"User successfully logged in.")
	    .done()
	},
	'Can user register?': function (test) {
		test
		    .click('#continue')
		    .click('#googlesigninButton')
		    .wait(10000)
		    .click('.btn-success')
			.wait(10000)
		    .click('#skip')
		    .wait(10000)
		    .assert.title().is('Unclutter', 'User successfully registered.')
			.done()
	},
	'Can user retrieve topics?' : function (test) {
		test
			.open('http://bubbles.iminsys.com')
			.wait(2000)
			.assert.css('#loadingAlertError','visible',false,'US')
			.done()
	},
	'Can user log out?': function (test) {
		test
			.open('http://bubbles.iminsys.com')
		    .assert.title().is('Unclutter', 'User successfully registered.')
			.click('#logout')
			.wait(10000)
		    .assert.title().is('Register', 'Webpage has the correct title.')
		    .done()

	}
};
