
dependencies = [
    'ngRoute',
    'ui.bootstrap',
    'toaster',
    'PrivateBook.filters',
    'PrivateBook.services',
    'PrivateBook.controllers',
    'PrivateBook.directives',
    'PrivateBook.common',
    'PrivateBook.routeConfig',
    'audioPlayer-directive'
]

app = angular.module('PrivateBook', dependencies)

angular.module('PrivateBook.routeConfig', ['ngRoute'])
    .config ($routeProvider) ->
        $routeProvider
            .when('/', {
                templateUrl: '/assets/partials/view.html'
            })
            .when('/users/create', {
                templateUrl: '/assets/partials/create.html'
            })
            .when('/users/login',{
                templateUrl: '/assets/partials/login.html'
            })  
            .otherwise({redirectTo: '/'})

@commonModule = angular.module('PrivateBook.common', [])
@controllersModule = angular.module('PrivateBook.controllers', [])
@servicesModule = angular.module('PrivateBook.services', [])
@modelsModule = angular.module('PrivateBook.models', [])
@directivesModule = angular.module('PrivateBook.directives', [])
@filtersModule = angular.module('PrivateBook.filters', [])