const CONTEXT_MESSAGE_COUNT = 5;
const API_HELLO_URL = 'https://acme-fitness-store-gateway-7d0d7.svc.azuremicroservices.io/ai/hello';
const API_QUESTION_URL = 'https://acme-fitness-store-gateway-7d0d7.svc.azuremicroservices.io/ai/question';
const API_HEADER = {
  "Content-Type": "application/json"
};

let MESSAGE_HISTORY = [];
let CURRENT_CONVERSATION_ID = null;

function addMessage(message, senderIsAI) {
  MESSAGE_HISTORY.push({
    content: message,
    role: senderIsAI ? 'assistant' : 'user'
  });
  localStorage[CURRENT_CONVERSATION_ID] = JSON.stringify(MESSAGE_HISTORY);

  renderMessage(message, senderIsAI);
}

function renderMessage(message, senderIsAI) {
  const sender = senderIsAI ? 'ai' : 'customer';
  const messageElement = $(`<div class="message ${sender}"></div>`);
  const messageSpan = $('<div></div>');

  if (senderIsAI) {
    message = parseProductLink(message);
    messageSpan.html(marked.parse(message));
  } else {
    messageSpan.text(message);
  }
  messageElement.append(messageSpan);
  $('#aiChatHistory').append(messageElement);
  $('#aiChatHistory').scrollTop($('#aiChatHistory').prop('scrollHeight'));
}

function parseProductLink(message) {
  return message.replace(/{{(.*?)\|([^\|]+)}}/g, '[$1](/detail.html?id=$2)');
}

function filterProductMetadata(message) {
  return message.replace(/{{(.*?)\|([^\|]+)}}/g, '$1');
}

async function sendMessage() {
  let message = $('#aiChatInputbox').val()?.trim();
  $('#aiChatInputbox').val('');
  if (!message) return;

  const productId = window.location.search.split('?id=')[1] ?? '';

  $('#aiChatInputboxBuiltInQuestions').hide();
  addMessage(message, false);
  $('#aiChatInputboxTyping').show();

  const findUserRoleMessageIndexes = [];
  MESSAGE_HISTORY.forEach((message, index) => {
    if (message.role === 'user') {
      findUserRoleMessageIndexes.push(index);
    }
  });
  const firstUserRoleMessageIndexInContext = findUserRoleMessageIndexes.find(index => index >= MESSAGE_HISTORY.length - CONTEXT_MESSAGE_COUNT);

  let context = MESSAGE_HISTORY.slice(firstUserRoleMessageIndexInContext - MESSAGE_HISTORY.length);
  context = JSON.parse(JSON.stringify(context));
  context.map(message => {
    message.content = filterProductMetadata(message.content);
    return message;
  });

  try {
    const response = await fetch(API_QUESTION_URL, {
      method: 'POST',
      headers: API_HEADER ?? {},
      body: JSON.stringify({ messages: context, productId })
    });
    const data = await response.json();
  
    addMessage(data.messages[0], true);
  } finally {
    $('#aiChatInputboxTyping').hide();
  }
}

function getCurrentPage() {
  const page = location.pathname.replace(/^\/|\.html$/g, '') || 'home';
  return page;
}

function generateConversationId() {
  return crypto.randomUUID();
}

function clearCurrentConversation() {
  if (!CURRENT_CONVERSATION_ID) {
    return;
  }

  MESSAGE_HISTORY = [];
  localStorage[CURRENT_CONVERSATION_ID] = JSON.stringify(MESSAGE_HISTORY);

  $('#aiChatHistory').empty();
}

function restoreConversation(conversationId) {
  CURRENT_CONVERSATION_ID = conversationId;
  MESSAGE_HISTORY = JSON.parse(localStorage[CURRENT_CONVERSATION_ID]);
  MESSAGE_HISTORY.forEach(item => renderMessage(item.content, item.role === 'assistant'));
}

async function createNewConversation() {
  const conversationId = generateConversationId();
  const page = getCurrentPage();

  CURRENT_CONVERSATION_ID = conversationId;
  localStorage.CURRENT_CONVERSATION_ID = conversationId;

  try {
    $('#aiChatInputboxTyping').show();
    const response = await fetch(API_HELLO_URL, {
      method: 'POST',
      headers: API_HEADER ?? {},
      body: JSON.stringify({ conversationId, page })
    });
    const data = await response.json();
    const { greeting, suggestedPrompts } = data;

    if (greeting) {
      addMessage(greeting, true);
    }

    if (suggestedPrompts?.length) {
      const questionsElement = $('#aiChatInputboxBuiltInQuestions');
      suggestedPrompts.forEach(question => {
        const questionElement = $(`<button class="question">${question}</button>`);
        questionElement.click(() => {
          $('#aiChatInputbox').val(question);
          sendMessage();
        });
        questionsElement.append(questionElement);
      });
      questionsElement.show();
    }

    localStorage.CURRENT_CONVERSATION_ID = conversationId;
    CURRENT_CONVERSATION_ID = conversationId;
  } finally {
    $('#aiChatInputboxTyping').hide();
  }
}

function initConversation() {
  if (localStorage.CURRENT_CONVERSATION_ID) {
    return restoreConversation(localStorage.CURRENT_CONVERSATION_ID);
  }

  createNewConversation();
}

function changeChatToggle() {
  localStorage.chatToggleClosed = $('#aiChatToggle').prop('checked');
}

function aiChatToggleOpen() {
  $('#aiChatToggle').prop('checked', false);
  localStorage.chatToggleClosed = false;
  $('#aiChatHistory').scrollTop($('#aiChatHistory').prop('scrollHeight'));
}

function init() {
  $('#aiChatInputboxSendButton').click(sendMessage);
  $('#aiChatInputbox').keypress(function (e) {
    if (e.which == 13) {
      sendMessage();
      e.preventDefault();
      return false;
    }
  });
  $('#aiChatTitleClearButton').click(clearCurrentConversation);
  $('#aiChatToggle').change(changeChatToggle);
  $('#aiChatToggle').prop('checked', localStorage.chatToggleClosed !== 'false');
  $('body').delegate('#aiChatAskBtn', 'click', aiChatToggleOpen);
  
  let markedReadyWatcher;
  markedReadyWatcher = setInterval(() => {
    if (marked) {
      clearInterval(markedReadyWatcher);
      initConversation();
    }
  }, 100);
}

init();