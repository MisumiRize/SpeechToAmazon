const Dotenv = require('dotenv')

Dotenv.config()

const koa = require('koa')
const route = require('koa-route')
const parse = require('co-body')
const fetch = require('isomorphic-fetch')

const {createCart} = require('slack-amazon-bot/lib/amazon')
const createStorage = require('slack-amazon-bot/lib/firebase_storage')

const app = koa()

createStorage({
  firebase_uri: process.env.FIREBASE_URI,
  firebase_email: process.env.FIREBASE_EMAIL,
  firebase_password: process.env.FIREBASE_PASSWORD
}, (storage) => {

  function teamsGet(key) {
    return new Promise((resolve, reject) => {
      storage.teams.get(key, (err, data) => {
        if (err) {
          reject(err)
        } else {
          resolve(data)
        }
      })
    })
  }

  app.use(route.post('/cart', function *() {
    const data = yield parse.json(this)
    const asin = yield teamsGet(data.item)
    if (!asin) {
      this.throw(403, JSON.stringify({code: 403, message: 'Forbidden'}))
    }
    const res = yield createCart(asin.value)
    yield fetch(process.env.SLACK_WEBHOOK_URL, {
      method: 'post',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({text: `Checkout: ${res.purchase_url}`})
    })
    this.body = JSON.stringify({code: 200, message: 'Created'})
  }))

  app.listen(3000)
})

