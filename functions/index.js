'use strict';
const functions = require('firebase-functions');
const request = require('request-promise');
const nodemailer = require('nodemailer');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// Configure the email transport using the default SMTP transport and a GMail account.
// For Gmail, enable these:
// 1. https://www.google.com/settings/security/lesssecureapps
// 2. https://accounts.google.com/DisplayUnlockCaptcha
// For other types of transports such as Sendgrid see https://nodemailer.com/transports/
// TODO: Configure the `gmail.email` and `gmail.password` Google Cloud environment variables.
const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;

const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  },
});

const APP_NAME = 'onTime';

/** 
 * Sends a welcome email to new user.
 */
exports.sendWelcomeEmail = functions.auth.user().onCreate((user) => {
	const email = user.email; // The email of the user
	const displayName = 'bob'; //user.displayName; // The display name of the user.

	return sendWelcomeEmail(email, displayName);
});

/**
 * Sends a bye email to user.
 */
exports.sendByeEmail = functions.auth.user().onDelete((user) => {
	const email = user.email;
	const displayName = 'bob';

	return sendGoodbyeEmail(email, displayName);
});

// Sends a welcome email to the given user.
async function sendWelcomeEmail(email, displayName) {
	const mailOptions = {
		from: `${APP_NAME} <noreply@firebase.com>`,
		to: email,
	};

	// The user joined the application
	mailOptions.subject = `Welcome to ${APP_NAME}!`;
	mailOptions.text = `Hey ${displayName || ''}! Welcome to ${APP_NAME}. I hope you will enjoy our service.`;
	await mailTransport.sendMail(mailOptions);
	console.log('New welcome email sent to:', email);
	return null;
}

// Sends a goodbye email to the given user.
async function sendGoodbyeEmail(email, displayName) {
	const mailOptions = {
		from: `${APP_NAME} <noreply@firebase.com>`,
		to: email,
	};

	mailOptions.subject = `Bye!`;
	mailOptions.text = `Hey ${displayName || ''}!, We confirm that we have deleted your ${APP_NAME} account.`;
	await mailTransport.sendMail(mailOptions);
	console.log('Account deletion confirmation email sent to:', email);
	return null;
}
