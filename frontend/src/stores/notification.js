import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

export const useNotificationStore = defineStore('notification', () => {
  const messages = ref([])
  const announcements = ref([])
  const unreadCount = ref(0)
  const client = ref(null)

  async function loadUnread() {
    const data = await api.listUnreadNotifications()
    messages.value = data
    unreadCount.value = data.length
  }

  async function markRead(ids) {
    await api.markNotificationsRead(ids)
    messages.value = messages.value.map((m) => (ids.includes(m.id) ? { ...m, status: 'READ' } : m))
    unreadCount.value = messages.value.filter((m) => m.status === 'UNREAD').length
  }

  function connect(token) {
    if (client.value) {
      client.value.deactivate()
    }
    if (!token) return
    const stompClient = new Client({
      webSocketFactory: () => new SockJS('/api/ws'),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      onConnect: () => {
        stompClient.subscribe('/user/queue/notice', (msg) => {
          const body = JSON.parse(msg.body)
          messages.value.unshift(body)
          unreadCount.value += 1
        })
        stompClient.subscribe('/topic/announcement', (msg) => {
          const body = JSON.parse(msg.body)
          announcements.value.unshift(body)
        })
      },
      reconnectDelay: 5000,
    })
    stompClient.activate()
    client.value = stompClient
  }

  function disconnect() {
    if (client.value) {
      client.value.deactivate()
      client.value = null
    }
  }

  return {
    messages,
    announcements,
    unreadCount,
    loadUnread,
    markRead,
    connect,
    disconnect,
  }
})
