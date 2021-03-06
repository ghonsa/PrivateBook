angular.module('audioPlayer-directive', [])
    .directive('audioPlayer', function($rootScope) {
        return {
            restrict: 'E',
            scope: {},
            controller: function($scope, $element) {
                $scope.audio = new Audio();
                $scope.currentNum = 0;
                console.log('ap');
                // tell others to give me my prev/next track (with audio.set message)
                $scope.next = function(){console.log('next'); $rootScope.$broadcast('audio.next'); };
                $scope.prev = function(){ $rootScope.$broadcast('audio.prev'); };

                // tell audio element to play/pause, you can also use $scope.audio.play() or $scope.audio.pause();
                $scope.playpause = function(){ var a = $scope.audio.paused ? $scope.audio.play() : $scope.audio.pause(); };

                // listen for audio-element events, and broadcast stuff
                $scope.audio.addEventListener('play', function(){ console.log('play'); $rootScope.$broadcast('audio.play', this); });
                $scope.audio.addEventListener('pause', function(){  console.log('pause'); $rootScope.$broadcast('audio.pause', this); });
                //$scope.audio.addEventListener('timeupdate', function(){ console.log('timeupdate'); $rootScope.$broadcast('audio.time', this); });
                $scope.audio.addEventListener('ended', function(){ $rootScope.$broadcast('audio.ended', this);  });

                // set track & play it
                $rootScope.$on('audio.set', function(r, file, info, currentNum, totalNum){
                    console.log("audio.set")
                    var playing = !$scope.audio.paused;
                    $scope.audio.src = file;
                    var a = playing ? $scope.audio.play() : $scope.audio.pause();
                    $scope.info = info;
                    $scope.currentNum = currentNum;
                    $scope.totalNum = totalNum;
                   
                });

                $rootScope.$on('audio.play', function(){
                  $scope.audio.play()
                });

                // update display of things - makes time-scrub work
                setInterval(function(){ $scope.$apply(); }, 500);
            },

            templateUrl: 'assets/javascripts/component.html'
        };
    });