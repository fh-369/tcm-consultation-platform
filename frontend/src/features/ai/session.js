const DEFAULT_TITLE = '新的对话'
const CONTEXT_MESSAGE_LIMIT = 12

function createId() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}

function now() {
  return new Date().toISOString()
}

function trimTitle(value) {
  const title = String(value || '').trim()
  return title ? title.slice(0, 24) : DEFAULT_TITLE
}

export function createConversation(seed = '') {
  const createdAt = now()
  return {
    id: createId(),
    title: trimTitle(seed),
    createdAt,
    updatedAt: createdAt,
    messages: [],
  }
}

export function createUserMessage(content) {
  return {
    id: createId(),
    role: 'user',
    content: String(content || '').trim(),
    createdAt: now(),
  }
}

export function createAssistantMessage(content, options = {}) {
  return {
    id: createId(),
    role: 'assistant',
    content: String(content || '').trim(),
    fallback: Boolean(options.fallback),
    disclaimer: options.disclaimer || '',
    createdAt: now(),
  }
}

export function summarizeConversation(conversation) {
  const firstUserMessage = conversation?.messages?.find((message) => message.role === 'user')
  return trimTitle(firstUserMessage?.content || conversation?.title)
}

export function buildAIContext(messages = [], limit = CONTEXT_MESSAGE_LIMIT) {
  return messages
    .filter((message) => ['user', 'assistant'].includes(message.role))
    .map((message) => ({
      role: message.role,
      content: String(message.content || '').trim(),
    }))
    .filter((message) => message.content)
    .slice(-limit)
}

export function removeEmptyConversations(conversations = []) {
  return conversations.filter((conversation) => conversation?.messages?.length > 0)
}

export function removeConversation(conversations = [], id) {
  const nextConversations = conversations.filter((conversation) => conversation.id !== id)
  return {
    conversations: nextConversations,
    activeId: nextConversations[0]?.id || '',
  }
}
