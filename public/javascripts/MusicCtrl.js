  'use strict'; 
  
   var app = angular.module('PrivateBook',[]);
   
   app.controller('MusicCtrl', ['$scope','$http','$rootScope','Mp3Service',
                     function ( $scope, $http, $rootScope,Mp3Service) {
        $scope.currentTrack = 0;
        $scope.pageSize = 5;
        $scope.Mp3Service = Mp3Service;
        $scope.pageSize = 5; 
        $scope.mp3s = [];
        $scope.mp3 = [];
        $scope.mp3Data = [];
        $scope.title = [];
        $scope.getAllMp3s();
        $scope.isDebug = false;
      
        var updateTrack = function(){
           $rootScope.$broadcast('audio.set', 'mp3/' + this.mp3s[this.currentTrack].filePath, this.mp3s[this.currentTrack], this.currentTrack, this.mp3s.length);
        };

        $scope.$on('audio.next', function(){
            $scope.currentTrack++;
            if ($scope.currentTrack < $scope.data.length){
                updateTrack();
            }else{
                $scope.currentTrack=$scope.data.length-1;
            }
        });

        $scope.$on('audio.prev', function(){
            $scope.currentTrack--;
            if ($scope.currentTrack >= 0){
                updateTrack();
            }else{
                $scope.currentTrack = 0;
            }
        });
        
    $scope.next = function() {
      console.log('Next ' + this.currentTrack);
      this.currentTrack++;
      if (this.currentTrack < this.mp3s.length) {
        return this.updateTrack();
      } else {
        return this.currentTrack = this.mp3s.length - 1;
      }
    };

    $scope.prev = function() {
      console.log('Prev ' + this.currentTrack);
      this.currentTrack--;
      if (this.currentTrack >= 0) {
        return this.updateTrack();
      } else {
        return this.currentTrack = 0;
      }
    };

   

   $scope.getAllMp3s = function() {
      var _this = this;
      this.$log.debug("getAllMp3s()");
      return this.Mp3Service.listMp3s().then(function(data) {
        _this.$log.debug("foo Promise returned " + data.length + " Mp3s");
        return _this.mp3s = angular.copy(data);
      }, function(error) {
        return _this.$log.error("Unable to get Mp3: " + error);
      });
    };

   $scope.playMp3 = function(mp3info) {
      this.$log.debug("playMp3s()" + mp3info);
      this.title = mp3info.filePath;
      this.mp3 = mp3info;
      return this.$rootScope.$broadcast('audio.set', 'mp3/' + this.mp3.filePath, this.mp3, 1, this.mp3s.length);
    };

       
    }]);
      