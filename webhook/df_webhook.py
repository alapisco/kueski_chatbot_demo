from flask import Flask
from flask import make_response
from flask import jsonify
from flask import request
from zenpy import Zenpy
from zenpy.lib.api_objects import Ticket

app = Flask(__name__)

creds = {
    'email' : 'alapisco@gmail.com',
    'token' : 'RtVvaMLToU7cOXlO4sv1Ngk9j62r6NHpknDDISQh',
    'subdomain': 'kueskihelp'
}

zenpy_client = Zenpy(**creds)

def results():

    req = request.get_json(force=True)
    parameters = req.get('queryResult').get('parameters')
    name = str(parameters.get('given-name'))	
    number = str(parameters.get(u'number'))	
    zenpy_client.tickets.create(Ticket(subject="User call request", description="Call user " + name + " to " + number))


@app.route('/webhook', methods=['GET', 'POST'])
def webhook():
    return make_response(jsonify(results()))

if __name__ == '__main__':
    app.run()
