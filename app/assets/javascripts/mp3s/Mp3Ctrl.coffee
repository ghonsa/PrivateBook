


class Mp3Ctrl
 
    constructor: (@$log, @$scope, @$rootScope, @Mp3Service) ->
        @$log.debug "constructing Mp3Controller"
        @currentTrack = 0
        @pageSize = 5
        @mp3s = []
        @mp3 = []
        @mp3Data = []
        @artists = []
        @artist = []
        @title = []
        @getAllMp3s()
        @getAllArtists()
        @isDebug = false
        @init( this)
   
 
    init: (us) ->
       @$scope.$on 'audio.next',() ->
         console.log('Next')
         us.next() 
     
       @$scope.$on 'audio.prev',() ->
          console.log('Prev ')
          us.prev()

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
      @$rootScope.$broadcast('audio.set', 'mp3/'+ @mp3s[@currentTrack]._id.$oid,  @mp3s[@currentTrack], @currentTrack, @mp3s.length);
      @$rootScope.$broadcast('audio.play',this) 
      for element in document.getElementsByTagName('*')
        if element.className is @mp3s[@currentTrack].songTitle
          element.scrollIntoView()
   
     getAllMp3s: () ->
       @$log.debug "getAllMp3s()"
       @Mp3Service.listMp3s()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Mp3s"
                @mp3s = angular.copy data
                index = 0
                (mp.index=index++) for mp in @mp3s               
            ,
            (error) =>
                @$log.error "Unable to get Mp3: #{error}"
            )
        
            
     getMp3sByArtist: (artist) ->
       @$log.debug "getMp3sByArtist(#{artist})"
       @Mp3Service.listMp3sByArtist(artist)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Mp3s"
                @mp3s = angular.copy data
                index = 0
                (mp.index=index++) for mp in @mp3s               
            ,
            (error) =>
                @$log.error "Unable to get Mp3: #{error}"
            )
            

     getAllArtists: () ->
       @$log.debug "getAllArtists()"
       @Mp3Service.listArtists()
        .then(
            (data) =>
                @$log.debug "f Promise returned #{data.length} artists"
                
                @artists = angular.copy data
                @$log.debug "Artists #{@artists} "              
            ,
            (error) =>
                @$log.error "Unable to get artists: #{error}"
            )
            
     playMp3:(mp3info)  ->
       @$log.debug "playMp3s()" + mp3info
       @title = mp3info.filePath
       @mp3 = mp3info
       @currentTrack= @mp3.index
       @updateTrack()
     
    
        
        
      
      controllersModule.controller('Mp3Ctrl', Mp3Ctrl)