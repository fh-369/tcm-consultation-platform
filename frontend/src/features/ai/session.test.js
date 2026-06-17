import { describe, expect, it } from 'vitest'

import {
  buildAIContext,
  createAssistantMessage,
  createConversation,
  createUserMessage,
  removeConversation,
  removeEmptyConversations,
  summarizeConversation,
} from './session'

describe('AI conversation session helpers', () => {
  it('creates local conversations with a readable default title', () => {
    const conversation = createConversation('最近容易疲倦，怎么调整作息？')

    expect(conversation.title).toBe('最近容易疲倦，怎么调整作息？')
    expect(conversation.messages).toEqual([])
  })

  it('summarizes conversations from the first user message', () => {
    const conversation = {
      title: '新的对话',
      messages: [
        createUserMessage('春季饮食有哪些温和建议？'),
        createAssistantMessage('可以保持饮食清淡。'),
      ],
    }

    expect(summarizeConversation(conversation)).toBe('春季饮食有哪些温和建议？')
  })

  it('builds recent AI context without sending unlimited history', () => {
    const messages = Array.from({ length: 16 }, (_, index) => (
      index % 2 === 0
        ? createUserMessage(`用户问题 ${index}`)
        : createAssistantMessage(`助手回答 ${index}`)
    ))

    const context = buildAIContext(messages)

    expect(context).toHaveLength(12)
    expect(context[0]).toEqual({ role: 'user', content: '用户问题 4' })
    expect(context.at(-1)).toEqual({ role: 'assistant', content: '助手回答 15' })
  })

  it('removes empty conversations when restoring saved sessions', () => {
    const started = createConversation('已开始')
    started.messages = [createUserMessage('我已经提问了')]
    const empty = createConversation()

    expect(removeEmptyConversations([empty, started])).toEqual([started])
  })

  it('removes a selected conversation and returns the next active id', () => {
    const first = createConversation('第一段对话')
    const second = createConversation('第二段对话')
    const result = removeConversation([first, second], first.id)

    expect(result.conversations).toEqual([second])
    expect(result.activeId).toBe(second.id)
  })
})
