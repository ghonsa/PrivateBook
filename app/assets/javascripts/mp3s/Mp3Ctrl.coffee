


class Mp3Ctrl
 
    constructor: (@$log, @$scope, @$rootScope, @Mp3Service) ->
        @$log.debug "constructing Mp3Controller"
        @currentTrack = 0
        @pageSize = 5
        @mp3s = []
        @mp3 = []
        @mp3Data = []
        @title = []
        @getAllMp3s()
        @isDebug = false
        @init( this)
   
 
    init: (us) ->
       @$scope.$on 'audio.next',() ->
         console.log('Next')
         us.next() 
     
       @$scope.$on 'audio.prev',() ->
          console.log('Prev ')
          us.prev

       @$rootScope.$on 'audio.ended',() ->
          console.log('Ended')
          us.next() 

                 
    next: () ->        
      console.log('Next ' + @currentTrack ) 
      @currentTrack++;
      if (@currentTrack <@mp3s.length)
        @updateTrack();
      else
        @currentTrack= @mp3s.length-1;
                 
    prev: () -> 
      console.log('Prev ' + @currentTrack)
      @currentTrack--;
      if (@currentTrack >= 0)
        @updateTrack();
      else
        @currentTrack = 0;
     
    updateTrack: () ->
      @$log.debug "updateTrack()"     
      @$rootScope.$broadcast('audio.set', 'mp3/'+ @mp3s[@currentTrack].filePath,  @mp3s[@currentTrack], @currentTrack, @mp3s.length);
       
   
     getAllMp3s: () ->
       @$log.debug "getAllMp3s()"
       @Mp3Service.listMp3s()
        .then(
            (data) =>
                @$log.debug "foo Promise returned #{data.length} Mp3s"
                @mp3s = angular.copy data
               
            ,
            (error) =>
                @$log.error "Unable to get Mp3: #{error}"
            )

     playMp3:(mp3info)  ->
       @$log.debug "playMp3s()" + mp3info
       @title = mp3info.filePath
       @mp3 = mp3info
       @$rootScope.$broadcast('audio.set', 'mp3/'+ @mp3.filePath,  @mp3, 1, @mp3s.length);
    
        
        
      
      controllersModule.controller('Mp3Ctrl', Mp3Ctrl)