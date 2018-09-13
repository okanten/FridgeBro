import requests,json
session = requests.Session()
token = session.get('https://spar.no/')

tokenJson = session.cookies.get_dict()
#print(tokenJson["_app_token_"])
headers = {
    'Referer': 'https://spar.no/sok?query=7311041013649',
    'x-csrf-token': tokenJson["_app_token_"],
}

params = (
    ('types', 'suggest,products,articles,recipes,stores'),
    ('search', '7311041013649'),
    ('page_size', '10'),
    ('suggest', 'true'),
    ('full_response', 'true'),
)




response = session.get('https://platform-rest-prod.ngdata.no/api/episearch/1210/all', headers={'x-csrf-token': session.cookies.get_dict()["_app_token_"],}, params=params)

#print(response.text)
jayson = json.loads(response.text)
jayson2 = jayson["products"]["hits"]
jayson3 = jayson2["hits"]

print("Pris:", jayson["products"]["hits"]["hits"][0]["contentData"]["_source"]["pricePerUnitOriginal"])
#NB. Original query string below. It seems impossible to parse and
#reproduce query strings 100% accurately so the one below is given
#in case the reproduced version is not "correct".
# response = requests.get('https://platform-rest-prod.ngdata.no/api/episearch/1210/all?types=suggest%2Cproducts%2Carticles%2Crecipes%2Cstores&search=7046110058718&page_size=10&suggest=true&full_response=true', headers=headers)
