/*global require, requirejs */
(function () {
    'use strict';

    requirejs.config({
        paths: {
            'angular': ['lib/angularjs/angular'],
            'angular-route': ['lib/angular-route/angular-route'],
            'g7app.module': ['app/g7app.module']
        },
        shim: {
            'angular': {
                exports: 'angular'
            },
            'angular-route': {
                deps: ['angular'],
                exports: 'angular'
            },
            'g7app.module': {
                deps: ['angular']
            }
        }
    });

    require(['g7app.module'], function () {
        angular.bootstrap(document, ['g7.app']);
    })
}());
