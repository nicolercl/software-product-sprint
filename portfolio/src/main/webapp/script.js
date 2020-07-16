// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}
async function loadPage(){
  getComments();
  checkLoginStatus();
}

async function getComments() {
  const commentElement = document.getElementById('comments-container');
  const response = await fetch('/data');
  const comments = await response.json();
  for (let i = 0; i < comments.length; i++){
    commentElement.appendChild(createCommentElement(comments[i]));
  }
}
function createCommentElement(comment) {
  const divElement = document.createElement('div');
  divElement.setAttribute("class", "project-card-text");
  divElement.appendChild(createCustomElement(comment['mUser'], 'h5'));
  divElement.appendChild(createCustomElement(comment['mContent'], 'p'));
  divElement.appendChild(createCustomElement('Timestamp: ' + comment['mTimestamp'], 'h5'));
  return divElement;
}
function createCustomElement(text, type) {
  const element = document.createElement(type);
  element.innerText = text;
  return element;
}
async function checkLoginStatus() {
  const commentForm = document.getElementById("comment-form");
  const loginMessageElement = document.getElementById("login-message");
  const response = await fetch('/login');
  const text = await response.text();
  if (text.trim() == "LoggedIn") {
    commentForm.style.display = "block";
    loginMessageElement.style.display = "none";
  } else {
    commentForm.style.display = "none";
    loginMessageElement.style.display = "block";
    loginMessageElement.innerHTML = text;
  }
}