import os


class EndpointConfiguration:
    def __init__(self, file_name):
        """
        Class constructor
        :param file_name: Input filename, found on /input
        """
        # Initialize variables
        self.videos = []
        self.video_request = {}
        self.endpoints = {}
        self.caches = {}

        file_path = os.path.dirname(os.path.abspath(__file__)) + '/../input/' + file_name

        with open(file_path, 'r') as file_object:
            file_config = file_object.readline().rstrip().split(' ')
            video_sizes = file_object.readline().rstrip().split(' ')

            # General information
            video_number = int(file_config[0])
            endpoint_number = int(file_config[1])
            requests_number = int(file_config[2])
            cache_number = int(file_config[3])
            self.cache_size = int(file_config[4])

            video_index = 0
            for video_size_item in video_sizes:
                self.videos.append(video_size_item)
                self.video_request[video_index] = []
                video_index += 1

            for endpoint_index in range(0, endpoint_number):
                endpoint_configuration = file_object.readline().rstrip().split(' ')

                endpoint_caches_number = int(endpoint_configuration[1])
                self.endpoints[endpoint_index] = {
                    'datacenter_latency': int(endpoint_configuration[0]),
                    'caches': {},
                    'video_request': {}
                }

                for endpoint_cache_index in range(0, endpoint_caches_number):
                    cache_configuration = file_object.readline().rstrip().split(' ')
                    cache_id = int(cache_configuration[0])
                    cache_latency = int(cache_configuration[1])

                    if cache_id not in self.caches:
                        self.caches[cache_id] = []
                    self.caches[cache_id].append(endpoint_index)

                    self.endpoints[endpoint_index]['caches'][cache_id] = cache_latency

            request_configuration = file_object.readline().rstrip().split(' ')
            while (len(request_configuration) > 1):
                video_id = int(request_configuration[0])
                video_endpoint = int(request_configuration[1])
                video_request_number = int(request_configuration[2])

                self.video_request[video_id].append(video_endpoint)

                self.endpoints[video_endpoint]['video_request'][video_id] = video_request_number

                request_configuration = file_object.readline().rstrip().split(' ')
